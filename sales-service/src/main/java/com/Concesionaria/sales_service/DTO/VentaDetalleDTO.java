package com.Concesionaria.sales_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VentaDetalleDTO {

    private Integer id;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private Integer vehiculoId;
    private BigDecimal subtotal;

}
