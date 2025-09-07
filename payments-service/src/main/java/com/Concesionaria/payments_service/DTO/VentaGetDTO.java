package com.Concesionaria.payments_service.DTO;

import com.Concesionaria.payments_service.util.EstadoVenta;
import com.Concesionaria.payments_service.util.FrecuenciaPago;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class VentaGetDTO {
    private Integer id;
    private LocalDate fecha;
    private BigDecimal total;
    private FrecuenciaPago frecuenciaPago;
    private Integer clienteId;
    private Integer userId;
    private Double entrega;
    private EstadoVenta estado;
    private Integer cuotas;
    private Boolean activo;
}
