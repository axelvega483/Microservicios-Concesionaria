package com.Concesionaria.auth_service.service;

import com.Concesionaria.auth_service.DTO.UserVentaDTO;
import com.Concesionaria.auth_service.config.security.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "sales-service",configuration = FeignClientConfig.class)
public interface VentaFeignClient {
    @GetMapping("/sales/user/{userId}")
    List<UserVentaDTO> obtenerVentasPorUser(@PathVariable Integer userId);
}
