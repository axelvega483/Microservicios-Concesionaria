package com.Concesionaria.payments_service.DTO;

import com.Concesionaria.payments_service.util.EstadoPagos;
import com.Concesionaria.payments_service.util.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDate;


public record PagosGetDTO(
        Integer id,
        LocalDate fechaPago,
        MetodoPago metodoPago,
        BigDecimal monto,
        EstadoPagos estado,
        boolean activo,
        Integer ventaId) {

}
