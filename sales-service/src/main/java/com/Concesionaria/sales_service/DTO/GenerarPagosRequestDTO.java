package com.Concesionaria.sales_service.DTO;

import com.Concesionaria.sales_service.util.FrecuenciaPago;
import java.math.BigDecimal;


public record GenerarPagosRequestDTO(
        Integer ventaId,
        BigDecimal totalVenta,
        BigDecimal entrega,
        FrecuenciaPago frecuenciaPago,
        Integer cuotas) {

}