package com.Concesionaria.sales_service.DTO;


import java.math.BigDecimal;
import java.time.LocalDate;

public record UserVentaDTO(
        Integer id,
        LocalDate fecha,
        BigDecimal total) {


}
