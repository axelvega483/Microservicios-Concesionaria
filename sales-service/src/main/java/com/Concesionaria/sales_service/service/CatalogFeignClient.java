package com.Concesionaria.sales_service.service;

import com.Concesionaria.sales_service.DTO.VehiculoDTO;
import com.Concesionaria.sales_service.DTO.VehiculoVentaDetalleDTO;
import com.Concesionaria.sales_service.config.security.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "catalog-service",configuration = FeignClientConfig.class)
public interface CatalogFeignClient {
    @GetMapping("/catalog/{vehiculoId}/venta")
    VehiculoDTO getVehiculoById(@PathVariable("vehiculoId") Integer vehiculoId);

    @PutMapping("/catalog/{vehiculoId}/incrementar-stock")
    VehiculoDTO incrementarStock(@PathVariable("vehiculoId") Integer vehiculoId,
                                             @RequestParam("cantidad") Integer cantidad);
    @PutMapping("/catalog/{vehiculoId}/descontar-stock")
    VehiculoDTO descontarStock(@PathVariable("vehiculoId") Integer vehiculoId,
                                             @RequestParam("cantidad") Integer cantidad);
}
