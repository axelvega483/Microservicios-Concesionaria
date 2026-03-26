package com.Concesionaria.customer_service.DTO;


import com.Concesionaria.customer_service.util.EstadoVenta;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClienteVentaDTO(
        Integer id,
        LocalDate fecha,
        BigDecimal total,
        EstadoVenta estado) {

}
