package com.Concesionaria.payments_service.DTO;

import com.Concesionaria.payments_service.util.EstadoVenta;
import com.Concesionaria.payments_service.util.FrecuenciaPago;


import java.math.BigDecimal;
import java.time.LocalDate;

public record VentaGetDTO(
        Integer id,
         LocalDate fecha,
         BigDecimal total,
         FrecuenciaPago frecuenciaPago,
         Integer clienteId,
         Integer userId,
         Double entrega,
         EstadoVenta estado,
         Integer cuotas,
         boolean activo) {

}
