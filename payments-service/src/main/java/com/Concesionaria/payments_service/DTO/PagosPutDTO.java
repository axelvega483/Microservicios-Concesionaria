package com.Concesionaria.payments_service.DTO;

import com.Concesionaria.payments_service.util.EstadoPagos;
import com.Concesionaria.payments_service.util.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagosPutDTO {
    private LocalDate fechaPago;
    private MetodoPago metodoPago;
    private EstadoPagos estado;
    private Boolean activo;
}
