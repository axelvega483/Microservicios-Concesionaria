package com.Concesionaria.sales_service.service;

import com.Concesionaria.sales_service.DTO.GenerarPagosRequestDTO;
import com.Concesionaria.sales_service.DTO.PagosDTO;
import com.Concesionaria.sales_service.config.security.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "payments-service",configuration = FeignClientConfig.class)
public interface PagosFeignClient {
    @GetMapping("/payments/venta/{ventaId}")
    List<PagosDTO> getPagosPorVenta(@PathVariable Integer ventaId);

    @PostMapping("/payments/generar")
    List<PagosDTO> generarPagos(@RequestBody GenerarPagosRequestDTO request);

    @PutMapping("/payments/{pagoId}/anular")
    void anularPago(@PathVariable("pagoId") Integer pagoId);

}
