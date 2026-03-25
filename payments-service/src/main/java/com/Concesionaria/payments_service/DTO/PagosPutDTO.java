package com.Concesionaria.payments_service.DTO;

import com.Concesionaria.payments_service.util.EstadoPagos;
import com.Concesionaria.payments_service.util.MetodoPago;

import java.time.LocalDate;


public record PagosPutDTO(
        LocalDate fechaPago,
        MetodoPago metodoPago,
        EstadoPagos estado) {

}
