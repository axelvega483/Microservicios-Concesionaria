package com.Concesionaria.sales_service.DTO;

import com.Concesionaria.sales_service.util.EstadoPagos;
import com.Concesionaria.sales_service.util.MetodoPago;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PagosDTO {
    private Integer id;
    private Integer ventaId;
    private LocalDate fechaPago;
    private MetodoPago metodoPago;
    private BigDecimal monto;
    private EstadoPagos estado;
    private Boolean activo;
}