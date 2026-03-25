package com.Concesionaria.customer_service.service;

import com.Concesionaria.customer_service.DTO.ClienteVentaDTO;
import com.Concesionaria.customer_service.config.security.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "sales-service",configuration = FeignClientConfig.class)
public interface VentaFeignClient {
    @GetMapping("/sales/cliente/{clienteId}")
    List<ClienteVentaDTO> obtenerVentasPorCliente(@PathVariable Integer clienteId);
}
