package com.Concesionaria.customer_service.service;

import com.Concesionaria.customer_service.DTO.ClienteVentaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "venta-service")
public interface VentaFeignClient {
    @PostMapping("/ventas/ids")
    List<ClienteVentaDTO> obtenerVentasPorIds(@RequestBody List<Integer> ids);
}
