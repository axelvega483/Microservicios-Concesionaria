package com.Concesionaria.sales_service.service;

import com.Concesionaria.sales_service.DTO.*;
import com.Concesionaria.sales_service.model.DetalleVenta;
import com.Concesionaria.sales_service.model.Venta;
import com.Concesionaria.sales_service.repository.VentaRepository;
import com.Concesionaria.sales_service.util.EstadoPagos;
import com.Concesionaria.sales_service.util.EstadoVenta;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class VentaService implements IVentaService {

    private static final Logger log = LoggerFactory.getLogger(VentaService.class);

    @Autowired
    private VentaRepository repo;

    @Autowired
    private MapperDTO mapper;

    @Autowired
    private PagosFeignClient pagosClient;

    @Autowired
    private CatalogFeignClient catalogClient;

    @Override
    public VentaGetDTO create(VentaPostDTO post) {
        log.info("Creando nueva venta para cliente ID: {}", post.clienteId());

        // 1. Validar stock antes de crear la venta
        validarStockDisponible(post);

        // 2. Crear la entidad venta
        Venta venta = mapper.fromPostDTO(post);
        if (venta.getDetalleVentas() != null) {
            venta.getDetalleVentas().forEach(detalle -> detalle.setVenta(venta));
        }

        // 3. Calcular total
        venta.calcularTotal();

        // 4. Validar entrega
        if (venta.getEntrega() != null && venta.getEntrega().compareTo(venta.getTotal()) > 0) {
            throw new IllegalArgumentException(
                    String.format("La entrega de $%.2f no puede ser mayor al total de $%.2f",
                            venta.getEntrega(), venta.getTotal())
            );
        }

        // 5. Guardar venta
        Venta ventaGuardada = repo.save(venta);
        log.info("Venta creada con ID: {}", ventaGuardada.getId());

        try {
            // 6. Descontar stock (operación crítica)
            descontarStock(ventaGuardada);

            // 7. Generar pagos (operación no crítica para la creación)
            generarPagos(ventaGuardada);

        } catch (Exception e) {
            log.error("Error en operaciones post-creación de venta {}: {}", ventaGuardada.getId(), e.getMessage());
            // Si falla el descuento de stock, debemos revertir la venta
            if (e instanceof IllegalArgumentException) {
                log.error("Error crítico: Stock insuficiente, eliminando venta {}", ventaGuardada.getId());
                repo.deleteById(ventaGuardada.getId());
                throw e;
            }
            // Para otros errores (pagos), logueamos pero no revertimos la venta
            log.warn("Venta creada pero con problemas en servicios auxiliares");
        }

        return mapper.toDTO(ventaGuardada, ventaGuardada.getTotal());
    }

    private void validarStockDisponible(VentaPostDTO post) {
        log.debug("Validando stock disponible para nueva venta");

        if (post.detalleVentas() == null || post.detalleVentas().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un detalle");
        }

        for (DetalleVentaPostDTO detalle : post.detalleVentas()) {
            try {
                VehiculoDTO vehiculo = catalogClient.getVehiculoById(detalle.vehiculoId());
                if (vehiculo.stock() < detalle.cantidad()) {
                    throw new IllegalArgumentException(
                            String.format("Stock insuficiente para vehículo ID %d. Disponible: %d, Solicitado: %d",
                                    detalle.vehiculoId(), vehiculo.stock(), detalle.cantidad())
                    );
                }
            } catch (Exception e) {
                log.error("Error validando stock para vehículo {}: {}", detalle.vehiculoId(), e.getMessage());
                throw new IllegalArgumentException("No se pudo validar stock con el catálogo de vehículos");
            }
        }
    }

    @CircuitBreaker(name = "payments-service", fallbackMethod = "generarPagosFallback")
    @Retry(name = "payments-service")
    private void generarPagos(Venta ventaGuardada) {
        log.info("Generando pagos para venta ID: {}", ventaGuardada.getId());

        GenerarPagosRequestDTO request = new GenerarPagosRequestDTO(
                ventaGuardada.getId(),
                ventaGuardada.getTotal(),
                ventaGuardada.getEntrega(),
                ventaGuardada.getFrecuenciaPago(),
                ventaGuardada.getCuotas()
        );
        pagosClient.generarPagos(request);
        log.info("Pagos generados exitosamente para venta {}", ventaGuardada.getId());
    }

    private void generarPagosFallback(Venta ventaGuardada, Throwable throwable) {
        log.warn("Fallback generando pagos para venta {}: {}", ventaGuardada.getId(), throwable.getMessage());
        // No lanzamos excepción, solo logueamos porque los pagos se pueden generar después
        log.info("Venta {} creada pero pagos pendientes de generación", ventaGuardada.getId());
    }

    @CircuitBreaker(name = "catalog-service", fallbackMethod = "descontarStockFallback")
    @Retry(name = "catalog-service")
    private void descontarStock(Venta ventaGuardada) {
        log.info("Descontando stock para venta ID: {}", ventaGuardada.getId());

        for (DetalleVenta detalle : ventaGuardada.getDetalleVentas()) {
            catalogClient.descontarStock(detalle.getVehiculoId(), detalle.getCantidad());
            log.debug("Stock descontado: vehículo {} - cantidad {}", detalle.getVehiculoId(), detalle.getCantidad());
        }
        log.info("Stock descontado exitosamente para venta {}", ventaGuardada.getId());
    }

    private void descontarStockFallback(Venta ventaGuardada, Throwable throwable) {
        log.error("FALLBACK CRÍTICO - Error al descontar stock para venta {}: {}",
                ventaGuardada.getId(), throwable.getMessage());
        throw new IllegalArgumentException(
                "No se pudo descontar el stock. Venta no completada.", throwable);
    }

    @Override
    @Transactional
    public VentaGetDTO anular(Integer id) {
        log.info("Anulando venta ID: {}", id);

        Venta venta = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + id));

        // Validaciones previas
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new IllegalStateException("La venta ya se encuentra anulada");
        }
        if (venta.getEstado() == EstadoVenta.FINALIZADO) {
            throw new IllegalStateException("No se puede anular una venta ya finalizada");
        }

        // Variable para tracking de operaciones completadas
        boolean pagosAnulados = false;
        boolean stockRestaurado = false;

        try {
            // 1. Anular pagos (operación compensable)
            anularPagos(venta);
            pagosAnulados = true;

            // 2. Restaurar stock (operación compensable)
            restaurarStock(venta);
            stockRestaurado = true;

            // 3. Actualizar estado
            venta.setEstado(EstadoVenta.ANULADA);
            Venta ventaActualizada = repo.save(venta);

            log.info("Venta {} anulada exitosamente", id);
            return construirDTOConPagos(ventaActualizada);

        } catch (Exception e) {
            log.error("Error anulando venta {}: {}", id, e.getMessage());

            // Intentar compensar operaciones completadas
            if (pagosAnulados && !stockRestaurado) {
                log.warn("Intentando revertir anulación de pagos para venta {}", id);
                try {
                    revertirAnulacionPagos(venta);
                } catch (Exception revertEx) {
                    log.error("No se pudo revertir anulación de pagos: {}", revertEx.getMessage());
                }
            }

            throw new EntityNotFoundException("Error al anular la venta: " + e.getMessage(), e);
        }
    }

    @CircuitBreaker(name = "payments-service", fallbackMethod = "anularPagosFallback")
    @Retry(name = "payments-service")
    private void anularPagos(Venta venta) {
        log.info("Procesando anulación de pagos para venta {}", venta.getId());

        List<PagosDTO> pagos = pagosClient.getPagosPorVenta(venta.getId());

        // Verificar si hay pagos efectuados
        boolean hayPagosEfectuados = pagos != null && pagos.stream()
                .anyMatch(pago -> pago.estado() == EstadoPagos.PAGADO);

        if (hayPagosEfectuados) {
            throw new IllegalStateException("No se puede anular la venta porque ya tiene pagos efectuados");
        }

        // Anular pagos pendientes
        if (pagos != null && !pagos.isEmpty()) {
            for (PagosDTO pago : pagos) {
                pagosClient.anularPago(pago.id());
                log.debug("Pago anulado: {}", pago.id());
            }
        }

        log.info("Pagos anulados exitosamente para venta {}", venta.getId());
    }

    private void anularPagosFallback(Venta venta, Throwable throwable) {
        log.error("Fallback anulación de pagos para venta {}: {}", venta.getId(), throwable.getMessage());
        throw new RuntimeException("No se pudo procesar la anulación de pagos", throwable);
    }

    private void revertirAnulacionPagos(Venta venta) {
        log.info("Revirtiendo anulación de pagos para venta {}", venta.getId());
        try {
            List<PagosDTO> pagos = pagosClient.getPagosPorVenta(venta.getId());
            if (pagos != null) {
                for (PagosDTO pago : pagos) {
                    pagosClient.anularPago(pago.id());
                }
            }
        } catch (Exception e) {
            log.error("Error revirtiendo anulación de pagos: {}", e.getMessage());
            throw e;
        }
    }

    @CircuitBreaker(name = "catalog-service", fallbackMethod = "restaurarStockFallback")
    @Retry(name = "catalog-service")
    private void restaurarStock(Venta venta) {
        log.info("Restaurando stock para venta {}", venta.getId());

        if (venta.getDetalleVentas() == null || venta.getDetalleVentas().isEmpty()) {
            log.debug("No hay vehículos para restaurar stock en venta {}", venta.getId());
            return;
        }

        for (DetalleVenta detalle : venta.getDetalleVentas()) {
            catalogClient.incrementarStock(detalle.getVehiculoId(), detalle.getCantidad());
            log.debug("Stock restaurado: vehículo {} +{} unidades",
                    detalle.getVehiculoId(), detalle.getCantidad());
        }

        log.info("Stock restaurado exitosamente para venta {}", venta.getId());
    }

    private void restaurarStockFallback(Venta venta, Throwable throwable) {
        log.error("FALLBACK - Error al restaurar stock para venta {}: {}",
                venta.getId(), throwable.getMessage());
        // No lanzamos excepción para no romper la anulación, pero logueamos el error
        log.warn("⚠️ Advertencia: La venta se anuló pero el stock NO fue restaurado correctamente");
    }

    @Override
    @Transactional
    public VentaGetDTO delete(Integer id) {
        log.info("Eliminando (soft delete) venta ID: {}", id);

        Venta venta = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + id));

        venta.setActivo(false);
        Venta ventaEliminada = repo.save(venta);

        log.info("Venta {} marcada como inactiva", id);
        return construirDTOConPagos(ventaEliminada);
    }

    @Override
    @CircuitBreaker(name = "payments-service", fallbackMethod = "findByIdVentaFallback")
    @Retry(name = "payments-service")
    public VentaGetDTO findById(Integer id) {
        log.debug("Buscando venta ID: {} (con pagos)", id);

        Venta venta = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + id));

        try {
            List<PagosDTO> pagos = pagosClient.getPagosPorVenta(id);
            BigDecimal totalPagado = calcularSumaPagosPagados(pagos);
            BigDecimal saldoRestante = venta.getTotal().subtract(totalPagado);
            actualizarEstadoVenta(venta, totalPagado);
            return mapper.toDTO(venta, saldoRestante,pagos);
        } catch (Exception e) {
            log.warn("Error obteniendo pagos para venta {}: {}", id, e.getMessage());
            return mapper.toDTO(venta, venta.getTotal());
        }
    }

    public VentaGetDTO findByIdVentaFallback(Integer id, Throwable throwable) {
        log.warn("Fallback findById({}): {}", id, throwable.getMessage());

        Venta venta = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
        return mapper.toDTO(venta, venta.getTotal());
    }

    @Override
    public List<VentaGetDTO> findAll() {
        return repo.findByActivoTrue()
                .stream()
                .map(venta -> mapper.toDTO(venta, venta.getTotal()))
                .collect(Collectors.toList());
    }


    private VentaGetDTO construirDTOConPagos(Venta venta) {
        try {
            List<PagosDTO> pagos = pagosClient.getPagosPorVenta(venta.getId());
            BigDecimal totalPagado = calcularSumaPagosPagados(pagos);
            BigDecimal saldoRestante = venta.getTotal().subtract(totalPagado);
            return mapper.toDTO(venta, saldoRestante);
        } catch (Exception e) {
            log.warn("Error construyendo DTO con pagos para venta {}: {}", venta.getId(), e.getMessage());
            return mapper.toDTO(venta, venta.getTotal());
        }
    }

    private void actualizarEstadoVenta(Venta venta, BigDecimal totalPagado) {
        EstadoVenta estadoAnterior = venta.getEstado();

        if (venta.getTotal().subtract(totalPagado).compareTo(BigDecimal.ZERO) <= 0) {
            venta.setEstado(EstadoVenta.FINALIZADO);
        } else if (venta.getEstado() != EstadoVenta.ANULADA) {
            venta.setEstado(EstadoVenta.ACTIVO);
        }

        if (estadoAnterior != venta.getEstado()) {
            repo.save(venta);
            log.debug("Estado de venta {} actualizado: {} → {}",
                    venta.getId(), estadoAnterior, venta.getEstado());
        }
    }

    private BigDecimal calcularSumaPagosPagados(List<PagosDTO> pagos) {
        if (pagos == null || pagos.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return pagos.stream()
                .filter(p -> p.estado() == EstadoPagos.PAGADO)
                .map(PagosDTO::monto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<VehiculoVentaDetalleDTO> obtenerVentasPorVehiculo(Integer vehiculoId) {
        log.debug("Obteniendo ventas para vehículo ID: {}", vehiculoId);

        return repo.findByDetalleVentas_VehiculoId(vehiculoId).stream()
                .flatMap(venta -> mapper.vehiculoVentaDetalleDTO(venta).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<UserVentaDTO> obtenerVentasPorUser(Integer userId) {
        log.debug("Obteniendo ventas para usuario ID: {}", userId);

        return repo.findByUserId(userId)
                .stream()
                .map(mapper::toUserVentaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClienteVentaDTO> obtenerVentasPorCliente(Integer clienteId) {
        log.debug("Obteniendo ventas para cliente ID: {}", clienteId);

        return repo.findByClienteId(clienteId)
                .stream()
                .map(mapper::toClienteVentaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void actualizarSaldo(Integer ventaId, BigDecimal montoPagado) {
        log.debug("Actualizando saldo para venta {} con monto: {}", ventaId, montoPagado);

        Venta venta = repo.findById(ventaId)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada con ID: " + ventaId));

        venta.actualizarSaldo(montoPagado);
        repo.save(venta);

        log.debug("Saldo actualizado para venta {}", ventaId);
    }
}