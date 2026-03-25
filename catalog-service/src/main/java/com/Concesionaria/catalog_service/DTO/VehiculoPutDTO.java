package com.Concesionaria.catalog_service.DTO;

import com.Concesionaria.catalog_service.util.EstadoVehiculo;
import com.Concesionaria.catalog_service.util.TipoVehiculo;

import java.util.List;

public record VehiculoPutDTO(
        String marca,
        String modelo,
        Integer anioModelo,
        Double precio,
        Integer stock,
        String color,
        TipoVehiculo tipo,
        EstadoVehiculo estado,
        Integer kilometraje,
        boolean activo,
        List<Integer> detalleVentasId) {

}
