package com.Concesionaria.sales_service.DTO;

import com.Concesionaria.sales_service.util.EstadoVenta;
import com.Concesionaria.sales_service.util.FrecuenciaPago;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class VentaGetDTO {
    private Integer id;
    private LocalDate fecha;
    private FrecuenciaPago frecuenciaPago;
    private BigDecimal total;
    private List<VentaDetalleDTO> detalleVentas;
    private Integer clienteId;
    private Integer userId;
    private Boolean activo;
    private Double entrega;
    private EstadoVenta estado;
    private Integer cuotas;
    private BigDecimal saldoRestante;

    private BigDecimal totalPagado;
    private Integer cantidadPagos;
    private BigDecimal montoUltimoPago;
    private LocalDate fechaUltimoPago;
}
