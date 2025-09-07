package com.Concesionaria.sales_service.service;

import com.Concesionaria.sales_service.DTO.GenerarPagosRequestDTO;
import com.Concesionaria.sales_service.DTO.PagosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "payments-service")
public interface PagosFeignClient {
    @GetMapping("/pagos/venta/{ventaId}")
    List<PagosDTO> getPagosPorVenta(@PathVariable Integer ventaId);

    @GetMapping("/pagos/ventas")
    List<PagosDTO> getPagosPorVentas(@RequestParam List<Integer> ventaIds);

    @PostMapping("/pagos/generar")
    List<PagosDTO> generarPagos(@RequestBody GenerarPagosRequestDTO request);
}
