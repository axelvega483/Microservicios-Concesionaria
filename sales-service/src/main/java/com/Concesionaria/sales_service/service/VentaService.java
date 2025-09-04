package com.Concesionaria.sales_service.service;

import com.Concesionaria.sales_service.DTO.*;
import com.Concesionaria.sales_service.model.DetalleVenta;
import com.Concesionaria.sales_service.model.Venta;
import com.Concesionaria.sales_service.repository.VentaRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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

        // Calcular el total primero
        venta.calcularTotal();

        // Validación explícita
        if (venta.getEntrega() != null && venta.getEntrega() > venta.getTotal().doubleValue()) {
            throw new IllegalArgumentException(
                    String.format("La entrega de $%.2f no puede ser mayor al total de $%.2f",
                            venta.getEntrega(), venta.getTotal().doubleValue())
            );
        }

        Venta ventaGuardada = repo.save(venta);
        return mapper.toDTO(ventaGuardada, ventaGuardada.getTotal());
    }

    @Override
    @Transactional
    public VentaGetDTO update(Integer id, VentaPutDTO put) {
        Venta venta = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        // Actualizar campos
        if (put.getTotal() != null) venta.setTotal(put.getTotal());
        if (put.getFrecuenciaPago() != null) venta.setFrecuenciaPago(put.getFrecuenciaPago());
        if (put.getClienteId() != null) venta.setClienteId(put.getClienteId());
        if (put.getUserId() != null) venta.setUserId(put.getUserId());
        if (put.getActivo() != null) venta.setActivo(put.getActivo());
        if (put.getEntrega() != null) venta.setEntrega(put.getEntrega());
        if (put.getEstado() != null) venta.setEstado(put.getEstado());
        if (put.getCuotas() != null) venta.setCuotas(put.getCuotas());

        // Actualizar detalles si se proporcionan
        if (put.getDetalleVentas() != null && !put.getDetalleVentas().isEmpty()) {
            venta.getDetalleVentas().clear();
            List<DetalleVenta> nuevosDetalles = put.getDetalleVentas().stream()
                    .map(mapper::fromDetallePostDTO)
                    .peek(detalle -> detalle.setVenta(venta))
                    .toList();
            venta.getDetalleVentas().addAll(nuevosDetalles);
            venta.calcularTotal();
        }

        Venta ventaActualizada = repo.save(venta);

        // Obtener datos de pagos actualizados
        try {
            BigDecimal totalPagado = pagosClient.getTotalPagado(id);
            BigDecimal saldoRestante = ventaActualizada.getTotal().subtract(totalPagado);
            VentaGetDTO dto = mapper.toDTO(ventaActualizada, saldoRestante);
            dto.setTotalPagado(totalPagado);
            return dto;
        } catch (Exception e) {
            return mapper.toDTO(ventaActualizada, ventaActualizada.getTotal());
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
            BigDecimal totalPagado = pagosClient.getTotalPagado(id);
            BigDecimal saldoRestante = ventaEliminada.getTotal().subtract(totalPagado);
            VentaGetDTO dto = mapper.toDTO(ventaEliminada, saldoRestante);
            dto.setTotalPagado(totalPagado);
            return dto;
        } catch (Exception e) {
            return mapper.toDTO(ventaEliminada, ventaEliminada.getTotal());
        }
    }

    @Override
    @CircuitBreaker(name = "pagos-service", fallbackMethod = "findByIdVentaNoPago")
    @Retry(name = "pagos-service")
    public VentaGetDTO findById(Integer id) {
        Venta venta = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        BigDecimal totalPagado = pagosClient.getTotalPagado(id);
        Integer cantidadPagos = pagosClient.getCantidadPagos(id);
        BigDecimal ultimoPago = pagosClient.getMontoUltimoPago(id);

        BigDecimal saldoRestante = venta.getTotal().subtract(totalPagado);

        VentaGetDTO dto = mapper.toDTO(venta, saldoRestante);
        dto.setTotalPagado(totalPagado);
        dto.setCantidadPagos(cantidadPagos);
        dto.setMontoUltimoPago(ultimoPago);

        return dto;
    }

    public VentaGetDTO findByIdVentaNoPago(Integer id, Throwable throwable) {
        System.out.println("Fallback ejecutado para venta ID: " + id + ". Error: " + throwable.getMessage());

        Venta venta = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        VentaGetDTO dto = mapper.toDTO(venta, venta.getTotal());
        dto.setTotalPagado(BigDecimal.ZERO);
        dto.setCantidadPagos(0);
        dto.setMontoUltimoPago(BigDecimal.ZERO);

        return dto;
    }

    @Override
    @CircuitBreaker(name = "pagos-service", fallbackMethod = "findAllVentasNoPago")
    public List<VentaGetDTO> findAll() {
        List<Venta> ventas = repo.findByActivoTrue();

        return ventas.stream().map(venta -> {
            try {
                BigDecimal totalPagado = pagosClient.getTotalPagado(venta.getId());
                BigDecimal saldoRestante = venta.getTotal().subtract(totalPagado);
                VentaGetDTO dto = mapper.toDTO(venta, saldoRestante);
                dto.setTotalPagado(totalPagado);
                return dto;
            } catch (Exception e) {
                return mapper.toDTO(venta, venta.getTotal());
            }
        }).toList();
    }

    public List<VentaGetDTO> findAllVentasNoPago(Throwable throwable) {
        System.out.println("Fallback ejecutado para findAll. Error: " + throwable.getMessage());

        return repo.findByActivoTrue().stream()
                .map(venta -> {
                    VentaGetDTO dto = mapper.toDTO(venta, venta.getTotal());
                    dto.setTotalPagado(BigDecimal.ZERO);
                    return dto;
                })
                .toList();
    }

    @Override
    public List<VehiculoVentaDetalleDTO> obtenerVentasPorVehiculo(Integer vehiculoId) {
        return repo.findByDetalleVentas_VehiculoId(vehiculoId).stream()
                .flatMap(venta -> {
                    try {
                        return mapper.vehiculoVentaDetalleDTO(venta).stream();
                    } catch (Exception e) {
                        // Fallback: convertir cada detalle de la venta
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
                        UserVentaDTO dto = mapper.toUserVentaDTO(venta);
                        return dto;
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
                        ClienteVentaDTO dto = mapper.toClienteVentaDTO(venta);
                        return dto;
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
}
