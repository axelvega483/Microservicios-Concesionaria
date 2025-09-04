package com.Concesionaria.sales_service.DTO;

import com.Concesionaria.sales_service.model.DetalleVenta;
import com.Concesionaria.sales_service.model.Venta;
import com.Concesionaria.sales_service.util.EstadoVenta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class MapperDTO {
    public VentaGetDTO toDTO(Venta venta, BigDecimal saldoRestante) {
        VentaGetDTO dto = new VentaGetDTO();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setFrecuenciaPago(venta.getFrecuenciaPago());
        dto.setTotal(venta.getTotal());
        dto.setClienteId(venta.getClienteId());
        dto.setUserId(venta.getUserId());
        dto.setActivo(venta.getActivo());
        dto.setEntrega(venta.getEntrega()); // ← Solo el monto, sin porcentaje
        dto.setEstado(venta.getEstado());
        dto.setCuotas(venta.getCuotas());
        dto.setSaldoRestante(saldoRestante);
        dto.setDetalleVentas(toDetalleDTOList(venta.getDetalleVentas()));
        return dto;
    }

    public List<VentaDetalleDTO> toDetalleDTOList(List<DetalleVenta> detalles) {
        if (detalles == null) return Collections.emptyList();

        return detalles.stream()
                .map(this::toDetalleDTO)
                .toList();
    }

    public VentaDetalleDTO toDetalleDTO(DetalleVenta detalle) {
        VentaDetalleDTO dto = new VentaDetalleDTO();
        dto.setId(detalle.getId());
        dto.setVehiculoId(detalle.getVehiculoId());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(detalle.getCantidad())));
        return dto;
    }

    public Venta fromPostDTO(VentaPostDTO postDTO) {
        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setFrecuenciaPago(postDTO.getFrecuenciaPago());
        venta.setClienteId(postDTO.getClienteId());
        venta.setUserId(postDTO.getUserId());
        venta.setEntrega(postDTO.getEntrega());
        venta.setCuotas(postDTO.getCuotas());
        venta.setEstado(EstadoVenta.ACTIVO);
        venta.setActivo(true);

        // Convertir detalles DTO a entidades
        if (postDTO.getDetalleVentas() != null) {
            List<DetalleVenta> detalles = postDTO.getDetalleVentas().stream()
                    .map(this::fromDetallePostDTO)
                    .toList();
            venta.setDetalleVentas(detalles);
        }

        return venta;
    }

    public DetalleVenta fromDetallePostDTO(DetalleVentaPostDTO postDTO) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setVehiculoId(postDTO.getVehiculoId());
        detalle.setCantidad(postDTO.getCantidad());
        detalle.setPrecioUnitario(postDTO.getPrecioUnitario());
        return detalle;
    }

    public UserVentaDTO toUserVentaDTO(Venta venta) {
        UserVentaDTO dto = new UserVentaDTO();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setTotal(venta.getTotal());
        return dto;
    }
    public List<VehiculoVentaDetalleDTO> vehiculoVentaDetalleDTO(Venta venta) {
        if (venta.getDetalleVentas() == null || venta.getDetalleVentas().isEmpty()) {
            return Collections.emptyList();
        }

        return venta.getDetalleVentas().stream()
                .map(detalle -> {
                    VehiculoVentaDetalleDTO dto = new VehiculoVentaDetalleDTO();
                    dto.setId(venta.getId()); // ID de la venta
                    dto.setCantidad(detalle.getCantidad());
                    dto.setPrecioUnitario(detalle.getPrecioUnitario());
                    return dto;
                })
                .toList();
    }
    public ClienteVentaDTO toClienteVentaDTO(Venta venta) {
        ClienteVentaDTO dto = new ClienteVentaDTO();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setTotal(venta.getTotal());
        return dto;
    }
}
