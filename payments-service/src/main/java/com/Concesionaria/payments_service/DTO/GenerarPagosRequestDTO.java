package com.Concesionaria.payments_service.DTO;

import com.Concesionaria.payments_service.util.FrecuenciaPago;

import java.math.BigDecimal;


public record GenerarPagosRequestDTO(
        Integer ventaId,
        BigDecimal totalVenta,
        Double entrega,
        FrecuenciaPago frecuenciaPago,
        Integer cuotas) {

}