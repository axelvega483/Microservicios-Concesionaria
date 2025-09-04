package com.Concesionaria.sales_service.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PagosDTO {
    private Integer id;
    private Integer ventaId;
    private LocalDate fechaPago;
    private String metodoPago;
    private BigDecimal monto;
    private String estado;
    private Boolean activo;
}