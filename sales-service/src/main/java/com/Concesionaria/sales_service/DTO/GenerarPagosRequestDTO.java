package com.Concesionaria.sales_service.DTO;

import com.Concesionaria.sales_service.util.FrecuenciaPago;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerarPagosRequestDTO {
    private Integer ventaId;
    private BigDecimal totalVenta;
    private Double entrega;
    private FrecuenciaPago frecuenciaPago;
    private Integer cuotas;
}