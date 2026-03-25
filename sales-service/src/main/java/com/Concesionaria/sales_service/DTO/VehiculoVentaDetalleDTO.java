package com.Concesionaria.sales_service.DTO;


import java.math.BigDecimal;


public record VehiculoVentaDetalleDTO(
        Integer id,
         Integer cantidad,
         BigDecimal precioUnitario) {

}
