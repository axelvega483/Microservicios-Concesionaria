package com.Concesionaria.sales_service.DTO;

import com.Concesionaria.sales_service.util.EstadoVenta;
import com.Concesionaria.sales_service.util.FrecuenciaPago;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class VentaPutDTO {
    private Integer id;
    private BigDecimal total;

    private FrecuenciaPago frecuenciaPago;

    private List<DetalleVentaPostDTO> detalleVentas;

    private Integer clienteId;

    private Integer userId;

    private Boolean activo;

    private Double entrega;

    private EstadoVenta estado;

    private Integer cuotas;
}
