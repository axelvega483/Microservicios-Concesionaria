package com.Concesionaria.sales_service.DTO;

import com.Concesionaria.sales_service.util.EstadoPagos;
import com.Concesionaria.sales_service.util.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDate;


public record PagosDTO(
        Integer id,
        Integer ventaId,
        LocalDate fechaPago,
        MetodoPago metodoPago,
        BigDecimal monto,
        EstadoPagos estado,
        boolean activo) {

}