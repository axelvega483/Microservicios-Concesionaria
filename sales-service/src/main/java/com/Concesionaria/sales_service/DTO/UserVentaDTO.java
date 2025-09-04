package com.Concesionaria.sales_service.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UserVentaDTO {

    private Integer id;
    private LocalDate fecha;
    private BigDecimal total;
}
