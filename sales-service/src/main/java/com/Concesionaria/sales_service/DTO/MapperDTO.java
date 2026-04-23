package com.Concesionaria.sales_service.DTO;

import com.Concesionaria.sales_service.model.DetalleVenta;
import com.Concesionaria.sales_service.model.Venta;
import com.Concesionaria.sales_service.util.EstadoVenta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class MapperDTO {

    public VentaGetDTO toDTO(Venta venta, BigDecimal saldoRestante, List<PagosDTO> pagos) {
        return new VentaGetDTO(
                venta.getId(),
                venta.getFecha(),
                venta.getFrecuenciaPago(),
                venta.getTotal(),
                toDetalleDTOList(venta.getDetalleVentas()),
                venta.getClienteId(),
                venta.getUserId(),
                venta.isActivo(),
                venta.getEntrega(),
                venta.getEstado(),
                venta.getCuotas(),
                saldoRestante,
                pagos
        );
    }

    public VentaGetDTO toDTOS(Venta venta) {
        return new VentaGetDTO(
                venta.getId(),
                venta.getFecha(),
                venta.getFrecuenciaPago(),
                venta.getTotal(),
                toDetalleDTOList(venta.getDetalleVentas()),
                venta.getClienteId(),
                venta.getUserId(),
                venta.isActivo(),
                venta.getEntrega(),
                venta.getEstado(),
                venta.getCuotas(),
                null,
                Collections.emptyList()
        );
    }

    public List<VentaDetalleDTO> toDetalleDTOList(List<DetalleVenta> detalles) {
        return Optional.ofNullable(detalles)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::toDetalleDTO)
                .toList();
    }

    public VentaDetalleDTO toDetalleDTO(DetalleVenta detalle) {
        return new VentaDetalleDTO(
                detalle.getId(),
                detalle.getVehiculoId(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad()))
        );
    }

    public Venta fromPostDTO(VentaPostDTO postDTO) {
        List<DetalleVenta> detalles = postDTO.detalleVentas()
                .stream()
                .map(this::fromDetallePostDTO)
                .toList();

        return Venta.builder()
                .fecha(LocalDate.now())
                .frecuenciaPago(postDTO.frecuenciaPago())
                .detalleVentas(detalles)
                .clienteId(postDTO.clienteId())
                .userId(postDTO.userId())
                .entrega(postDTO.entrega())
                .cuotas(postDTO.cuotas())
                .estado(EstadoVenta.ACTIVO)
                .activo(true)
                .build();
    }


    public DetalleVenta fromDetallePostDTO(DetalleVentaPostDTO postDTO) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setVehiculoId(postDTO.vehiculoId());
        detalle.setCantidad(postDTO.cantidad());
        detalle.setPrecioUnitario(postDTO.precioUnitario());
        return detalle;
    }

    public UserVentaDTO toUserVentaDTO(Venta venta) {
        return new UserVentaDTO(
                venta.getId(),
                venta.getFecha(),
                venta.getTotal(),
                venta.getEstado()
        );
    }

    public List<VehiculoVentaDetalleDTO> vehiculoVentaDetalleDTO(Venta venta) {
        return Optional.ofNullable(venta.getDetalleVentas())
                .orElse(Collections.emptyList())
                .stream()
                .map(detalle -> new VehiculoVentaDetalleDTO(
                        venta.getId(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getVenta().getEstado()
                ))
                .toList();
    }

    public ClienteVentaDTO toClienteVentaDTO(Venta venta) {
        return new ClienteVentaDTO(
                venta.getId(),
                venta.getFecha(),
                venta.getTotal(),
                venta.getEstado()
        );
    }
}