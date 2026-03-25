package com.Concesionaria.sales_service.DTO;


import java.math.BigDecimal;


public record VentaDetalleDTO(
        Integer id,
        Integer vehiculoId,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal) {


}
