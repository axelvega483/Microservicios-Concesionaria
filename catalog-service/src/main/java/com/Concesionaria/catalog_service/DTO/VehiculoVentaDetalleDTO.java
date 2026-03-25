package com.Concesionaria.catalog_service.DTO;


import java.math.BigDecimal;

public record VehiculoVentaDetalleDTO(
        Integer id,
        Integer cantidad,
        BigDecimal precioUnitario) {

}
