package com.Concesionaria.sales_service.service;

import com.Concesionaria.sales_service.DTO.*;
import com.Concesionaria.sales_service.model.Venta;
import com.Concesionaria.sales_service.repository.VentaRepository;
import com.Concesionaria.sales_service.util.EstadoPagos;
import com.Concesionaria.sales_service.util.EstadoVenta;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class VentaService implements IVentaService {

    @Autowired
    private VentaRepository repo;

    @Autowired
    private MapperDTO mapper;

    @Autowired
    private PagosFeignClient pagosClient;

    @Override
    @Transactional
    public VentaGetDTO create(VentaPostDTO post) {
        Venta venta = mapper.fromPostDTO(post);
        if (venta.getDetalleVentas() != null) {
            venta.getDetalleVentas().forEach(detalle -> detalle.setVenta(venta));
        }
        venta.calcularTotal();
        if (venta.getEntrega() != null && venta.getEntrega() > venta.getTotal().doubleValue()) {
            throw new IllegalArgumentException(
                    String.format("La entrega de $%.2f no puede ser mayor al total de $%.2f",
                            venta.getEntrega(), venta.getTotal().doubleValue())
            );
        }
        Venta ventaGuardada = repo.save(venta);
        try {
            GenerarPagosRequestDTO pagosRequest = new GenerarPagosRequestDTO();
            pagosRequest.setVentaId(ventaGuardada.getId());
            pagosRequest.setTotalVenta(ventaGuardada.getTotal());
            pagosRequest.setEntrega(ventaGuardada.getEntrega());
            pagosRequest.setFrecuenciaPago(ventaGuardada.getFrecuenciaPago());
            pagosRequest.setCuotas(ventaGuardada.getCuotas());

            List<PagosDTO> pagosCreados = pagosClient.generarPagos(pagosRequest);
            System.out.println("Pagos generados: " + pagosCreados.size() + " para venta ID: " + ventaGuardada.getId());

        } catch (Exception e) {
            System.err.println("⚠️  Venta creada pero error generando pagos: " + e.getMessage());
        }
        return mapper.toDTO(ventaGuardada, ventaGuardada.getTotal());
    }

    @Override
    @Transactional
    public VentaGetDTO update(Integer id, VentaPutDTO put) {
        Venta venta = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
        venta = mapper.fromPutDTO(venta, put);
        Venta ventaActualizada = repo.save(venta);
        try {
            List<PagosDTO> pagos = pagosClient.getPagosPorVenta(id);
            BigDecimal totalPagado = calcularSumaPagosPagados(pagos);
            Integer cantidadPagos = getCantidadPagosPagados(pagos);
            BigDecimal ultimoPago = getMontoUltimoPagoPagado(pagos);
            LocalDate fechaUltimoPago = getFechaUltimoPagoPagado(pagos);
            BigDecimal saldoRestante = ventaActualizada.getTotal().subtract(totalPagado);

            VentaGetDTO dto = mapper.toDTO(ventaActualizada, saldoRestante);
            dto.setTotalPagado(totalPagado);
            dto.setPagos(pagos);
            dto.setCantidadPagos(cantidadPagos);
            dto.setMontoUltimoPago(ultimoPago);
            dto.setFechaUltimoPago(fechaUltimoPago);
            return dto;
        } catch (Exception e) {
            VentaGetDTO dto = mapper.toDTO(ventaActualizada, ventaActualizada.getTotal());
            dto.setPagos(Collections.emptyList());
            dto.setCantidadPagos(0);
            dto.setMontoUltimoPago(BigDecimal.ZERO);
            dto.setFechaUltimoPago(null);
            return dto;
        }
    }

    @Override
    @Transactional
    public VentaGetDTO delete(Integer id) {
        Venta venta = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        venta.setActivo(false);
        Venta ventaEliminada = repo.save(venta);

        try {
            List<PagosDTO> pagos = pagosClient.getPagosPorVenta(id);
            BigDecimal totalPagado = calcularSumaPagosPagados(pagos);
            Integer cantidadPagos = getCantidadPagosPagados(pagos);
            BigDecimal ultimoPago = getMontoUltimoPagoPagado(pagos);
            LocalDate fechaUltimoPago = getFechaUltimoPagoPagado(pagos);
            BigDecimal saldoRestante = ventaEliminada.getTotal().subtract(totalPagado);

            VentaGetDTO dto = mapper.toDTO(ventaEliminada, saldoRestante);
            dto.setTotalPagado(totalPagado);
            dto.setPagos(pagos);
            dto.setCantidadPagos(cantidadPagos);
            dto.setMontoUltimoPago(ultimoPago);
            dto.setFechaUltimoPago(fechaUltimoPago);
            return dto;
        } catch (Exception e) {
            VentaGetDTO dto = mapper.toDTO(ventaEliminada, ventaEliminada.getTotal());
            dto.setPagos(Collections.emptyList());
            dto.setCantidadPagos(0);
            dto.setMontoUltimoPago(BigDecimal.ZERO);
            dto.setFechaUltimoPago(null);
            return dto;
        }
    }

    @Override
    @CircuitBreaker(name = "pagos-service", fallbackMethod = "findByIdVentaNoPago")
    @Retry(name = "pagos-service")
    public VentaGetDTO findById(Integer id) {
        Venta venta = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        List<PagosDTO> pagos = pagosClient.getPagosPorVenta(id);

        BigDecimal totalPagado = calcularSumaPagosPagados(pagos);
        Integer cantidadPagos = getCantidadPagosPagados(pagos);
        BigDecimal ultimoPago = getMontoUltimoPagoPagado(pagos);
        LocalDate fechaUltimoPago = getFechaUltimoPagoPagado(pagos);
        BigDecimal saldoRestante = venta.getTotal().subtract(totalPagado);
        actualizarEstadoVenta(venta, totalPagado);
        VentaGetDTO dto = mapper.toDTO(venta, saldoRestante);
        dto.setTotalPagado(totalPagado);
        dto.setCantidadPagos(cantidadPagos);
        dto.setMontoUltimoPago(ultimoPago);
        dto.setFechaUltimoPago(fechaUltimoPago);
        dto.setPagos(pagos);

        return dto;
    }

    public VentaGetDTO findByIdVentaNoPago(Integer id, Throwable throwable) {
        System.out.println("Fallback ejecutado para venta ID: " + id + ". Error: " + throwable.getMessage());

        Venta venta = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        VentaGetDTO dto = mapper.toDTO(venta, venta.getTotal());
        dto.setTotalPagado(BigDecimal.ZERO);
        dto.setPagos(Collections.emptyList());
        dto.setCantidadPagos(0);
        dto.setMontoUltimoPago(BigDecimal.ZERO);
        dto.setFechaUltimoPago(null);
        return dto;
    }

    @Override
    @CircuitBreaker(name = "pagos-service", fallbackMethod = "findAllVentasNoPago")
    @Retry(name = "pagos-service")
    public List<VentaGetDTO> findAll() {
        List<Venta> ventas = repo.findByActivoTrue();
        List<Integer> ventaIds = ventas.stream().map(Venta::getId).toList();

        List<PagosDTO> pagosList = pagosClient.getPagosPorVentas(ventaIds);

        return ventas.stream().map(venta -> {
            List<PagosDTO> pagos = pagosList.stream()
                    .filter(pago -> pago.getVentaId() != null && pago.getVentaId().equals(venta.getId()))
                    .collect(Collectors.toList());

            BigDecimal totalPagado = calcularSumaPagosPagados(pagos);
            Integer cantidadPagos = getCantidadPagosPagados(pagos);
            BigDecimal ultimoPago = getMontoUltimoPagoPagado(pagos);
            LocalDate fechaUltimoPago = getFechaUltimoPagoPagado(pagos);
            BigDecimal saldoRestante = venta.getTotal().subtract(totalPagado);
            actualizarEstadoVenta(venta, totalPagado);
            VentaGetDTO dto = mapper.toDTO(venta, saldoRestante);
            dto.setTotalPagado(totalPagado);
            dto.setPagos(pagos);
            dto.setCantidadPagos(cantidadPagos);
            dto.setMontoUltimoPago(ultimoPago);
            dto.setFechaUltimoPago(fechaUltimoPago);
            return dto;
        }).toList();
    }

    public List<VentaGetDTO> findAllVentasNoPago(Throwable throwable) {
        System.out.println("Fallback ejecutado para findAll. Error: " + throwable.getMessage());

        return repo.findByActivoTrue().stream()
                .map(venta -> {
                    VentaGetDTO dto = mapper.toDTO(venta, venta.getTotal());
                    dto.setTotalPagado(BigDecimal.ZERO);
                    dto.setPagos(Collections.emptyList());
                    dto.setCantidadPagos(0);
                    dto.setMontoUltimoPago(BigDecimal.ZERO);
                    dto.setFechaUltimoPago(null);
                    return dto;
                })
                .toList();
    }

    private void actualizarEstadoVenta(Venta venta, BigDecimal totalPagado) {
        if (venta.getTotal().subtract(totalPagado).compareTo(BigDecimal.ZERO) <= 0) {
            venta.setEstado(EstadoVenta.FINALIZADO);
        } else {
            venta.setEstado(EstadoVenta.ACTIVO);
        }
        repo.save(venta);  // Guardar el cambio de estado
    }

    private BigDecimal calcularSumaPagosPagados(List<PagosDTO> pagos) {
        if (pagos == null || pagos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return pagos.stream()
                .filter(pago -> pago != null && pago.getEstado() != null)
                .filter(pago -> pago.getEstado().equals(EstadoPagos.PAGADO))
                .map(PagosDTO::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Integer getCantidadPagosPagados(List<PagosDTO> pagos) {
        if (pagos == null || pagos.isEmpty()) {
            return 0;
        }
        return (int) pagos.stream()
                .filter(pago -> pago != null && pago.getEstado() != null)
                .filter(pago -> pago.getEstado().equals(EstadoPagos.PAGADO))
                .count();
    }

    private BigDecimal getMontoUltimoPagoPagado(List<PagosDTO> pagos) {
        if (pagos == null || pagos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return pagos.stream()
                .filter(pago -> pago != null && pago.getEstado() != null)
                .filter(pago -> pago.getEstado().equals(EstadoPagos.PAGADO))
                .max(Comparator.comparing(PagosDTO::getId))
                .map(PagosDTO::getMonto)
                .orElse(BigDecimal.ZERO);
    }

    private LocalDate getFechaUltimoPagoPagado(List<PagosDTO> pagos) {
        if (pagos == null || pagos.isEmpty()) {
            return null;
        }
        return pagos.stream()
                .filter(pago -> pago != null && pago.getEstado() != null)
                .filter(pago -> pago.getEstado().equals(EstadoPagos.PAGADO))
                .max(Comparator.comparing(PagosDTO::getFechaPago))
                .map(PagosDTO::getFechaPago)
                .orElse(null);
    }

    @Override
    public List<VehiculoVentaDetalleDTO> obtenerVentasPorVehiculo(Integer vehiculoId) {
        return repo.findByDetalleVentas_VehiculoId(vehiculoId).stream()
                .flatMap(venta -> {
                    try {
                        return mapper.vehiculoVentaDetalleDTO(venta).stream();
                    } catch (Exception e) {
                        return venta.getDetalleVentas().stream()
                                .filter(detalle -> detalle.getVehiculoId().equals(vehiculoId))
                                .map(detalle -> {
                                    VehiculoVentaDetalleDTO dto = new VehiculoVentaDetalleDTO();
                                    dto.setId(venta.getId());
                                    dto.setCantidad(detalle.getCantidad());
                                    dto.setPrecioUnitario(detalle.getPrecioUnitario());
                                    return dto;
                                });
                    }
                })
                .toList();
    }

    @Override
    public List<UserVentaDTO> obtenerVentasPorUser(Integer userId) {
        return repo.findByUserId(userId).stream()
                .map(venta -> {
                    try {
                        return mapper.toUserVentaDTO(venta);
                    } catch (Exception e) {
                        return mapper.toUserVentaDTO(venta);
                    }
                })
                .toList();
    }

    @Override
    public List<ClienteVentaDTO> obtenerVentasPorCliente(Integer clienteId) {
        return repo.findByClienteId(clienteId).stream()
                .map(venta -> {
                    try {
                        return mapper.toClienteVentaDTO(venta);
                    } catch (Exception e) {
                        ClienteVentaDTO dto = new ClienteVentaDTO();
                        dto.setId(venta.getId());
                        dto.setFecha(venta.getFecha());
                        dto.setTotal(venta.getTotal());
                        return dto;
                    }
                })
                .toList();
    }

    @Transactional
    public void actualizarSaldo(Integer ventaId, BigDecimal montoPagado) {
        Venta venta = repo.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + ventaId));

        venta.actualizarSaldo(montoPagado);
        repo.save(venta);
    }
}