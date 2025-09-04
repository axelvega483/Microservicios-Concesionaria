package com.Concesionaria.sales_service.service;

import com.Concesionaria.sales_service.DTO.PagosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "pagos-service")
public interface PagosFeignClient {
    @GetMapping("/pagos/venta/{ventaId}/total")
    BigDecimal getTotalPagado(@PathVariable Integer ventaId);

    @GetMapping("/pagos/venta/{ventaId}/cantidad")
    Integer getCantidadPagos(@PathVariable Integer ventaId);

    @GetMapping("/pagos/venta/{ventaId}/ultimo-pago")
    BigDecimal getMontoUltimoPago(@PathVariable Integer ventaId);

    // Opcional: para casos específicos donde necesites la lista completa
    @GetMapping("/pagos/venta/{ventaId}")
    List<PagosDTO> getPagosPorVenta(@PathVariable Integer ventaId);
}
