package com.Concesionaria.sales_service.DTO;


import com.Concesionaria.sales_service.util.EstadoVenta;

import java.math.BigDecimal;


public record VehiculoVentaDetalleDTO(
        Integer id,
        Integer cantidad,
        BigDecimal precioUnitario,
        EstadoVenta estado) {

}
