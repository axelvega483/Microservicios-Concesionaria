package com.Concesionaria.sales_service.DTO;

import jakarta.validation.constraints.NotNull;


import java.math.BigDecimal;

public record DetalleVentaPostDTO(
        @NotNull Integer vehiculoId,
        @NotNull Integer cantidad,
        @NotNull BigDecimal precioUnitario) {


}