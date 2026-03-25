package com.Concesionaria.catalog_service.DTO;

import com.Concesionaria.catalog_service.util.EstadoVehiculo;
import com.Concesionaria.catalog_service.util.TipoVehiculo;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VehiculoPostDTO(
        @NotNull
        String marca,
        @NotNull
        String modelo,
        @NotNull
        Integer anioModelo,
        @NotNull
        Double precio,
        @NotNull
        Integer stock,
        @NotNull
        String color,
        @NotNull
        TipoVehiculo tipo,
        @NotNull
        EstadoVehiculo estado,
        @NotNull
        Integer kilometraje,
        @NotNull
        List<String> nombresImagenes) {

}
