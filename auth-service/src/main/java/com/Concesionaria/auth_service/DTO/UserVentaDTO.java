package com.Concesionaria.auth_service.DTO;

import com.Concesionaria.auth_service.util.EstadoVenta;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserVentaDTO(
        Integer id,
        LocalDate fecha,
        BigDecimal total,
        EstadoVenta estado) {


}
