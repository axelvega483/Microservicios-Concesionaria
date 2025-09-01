package com.Concesionaria.catalog_service.DTO;

import com.Concesionaria.catalog_service.util.EstadoVehiculo;
import com.Concesionaria.catalog_service.util.TipoVehiculo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VehiculoGetDTO {

    private Integer id;
    private String marca;
    private String modelo;
    private Integer anioModelo;
    private Double precio;
    private Integer stock;
    private String color;
    private TipoVehiculo tipo;
    private EstadoVehiculo estado;
    private Integer kilometraje;
    private List<ImagenDTO> imagenes;
    private Boolean activo;
    private List<VehiculoVentaDetalleDTO> detalleVentas;
}