package com.Concesionaria.catalog_service.DTO;

import com.Concesionaria.catalog_service.util.EstadoVehiculo;
import com.Concesionaria.catalog_service.util.TipoVehiculo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class VehiculoPostDTO {
    @NotNull
    private String marca;
    @NotNull
    private String modelo;
    @NotNull
    private Integer anioModelo;
    @NotNull
    private Double precio;
    @NotNull
    private Integer stock;
    @NotNull
    private String color;
    @NotNull
    private TipoVehiculo tipo;
    @NotNull
    private EstadoVehiculo estado;
    @NotNull
    private Integer kilometraje;
    @NotNull
    private List<String> nombresImagenes;
    @NotNull
    private Boolean activo;
}
