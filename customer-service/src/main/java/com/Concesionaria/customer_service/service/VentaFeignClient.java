package com.Concesionaria.customer_service.service;

import com.Concesionaria.customer_service.DTO.ClienteVentaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "sales-service")
public interface VentaFeignClient {
    @GetMapping("/sales/cliente/{clienteId}")
    List<ClienteVentaDTO> obtenerVentasPorCliente(@PathVariable Integer clienteId);
}
