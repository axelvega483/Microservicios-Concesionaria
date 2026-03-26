package com.Concesionaria.catalog_service.DTO;


import com.Concesionaria.catalog_service.util.EstadoVenta;

import java.math.BigDecimal;

public record VehiculoVentaDetalleDTO(
        Integer id,
        Integer cantidad,
        BigDecimal precioUnitario,
        EstadoVenta estado) {

}
