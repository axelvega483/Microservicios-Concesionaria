package com.Concesionaria.catalog_service.service;

import com.Concesionaria.catalog_service.DTO.VehiculoVentaDetalleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "venta-service")
public interface VentaFeignClient {
    @GetMapping("/ventas/vehiculo/{vehiculoId}")
    List<VehiculoVentaDetalleDTO> obtenerVentasPorVehiculo(@PathVariable Integer vehiculoId);
}
