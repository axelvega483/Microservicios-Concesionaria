package com.Concesionaria.customer_service.DTO;


import java.math.BigDecimal;
import java.time.LocalDate;

public record ClienteVentaDTO(
         Integer id,
         LocalDate fecha,
         BigDecimal total) {

}
