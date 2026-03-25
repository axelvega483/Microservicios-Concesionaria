package com.Concesionaria.sales_service.DTO;

import com.Concesionaria.sales_service.util.EstadoVenta;
import com.Concesionaria.sales_service.util.FrecuenciaPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record VentaGetDTO(
        Integer id,
        LocalDate fecha,
        FrecuenciaPago frecuenciaPago,
        BigDecimal total,
        List<VentaDetalleDTO> detalleVentas,
        Integer clienteId,
        Integer userId,
        Boolean activo,
        BigDecimal entrega,
        EstadoVenta estado,
        Integer cuotas,
        BigDecimal saldoRestante,
        List<PagosDTO> pagos) {

}
