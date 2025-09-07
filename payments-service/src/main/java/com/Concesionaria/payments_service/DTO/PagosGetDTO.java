package com.Concesionaria.payments_service.DTO;

import com.Concesionaria.payments_service.util.EstadoPagos;
import com.Concesionaria.payments_service.util.MetodoPago;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PagosGetDTO {
    private Integer id;
    private LocalDate fechaPago;
    private MetodoPago metodoPago;
    private BigDecimal monto;
    private EstadoPagos estado;
    private Boolean activo;
    private Integer ventaId;
}
