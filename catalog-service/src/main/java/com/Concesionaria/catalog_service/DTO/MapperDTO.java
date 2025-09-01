package com.Concesionaria.catalog_service.DTO;

import com.Concesionaria.catalog_service.model.Vehiculo;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MapperDTO {
   public static VehiculoGetDTO toDTO(Vehiculo vehiculo){
        VehiculoGetDTO dto = new VehiculoGetDTO();
        dto.setId(vehiculo.getId());
        dto.setActivo(vehiculo.getActivo());
        dto.setEstado(vehiculo.getEstado());
        dto.setMarca(vehiculo.getMarca());
        dto.setColor(vehiculo.getColor());
        dto.setAnioModelo(vehiculo.getAnioModelo());
        dto.setKilometraje(vehiculo.getKilometraje());
        dto.setModelo(vehiculo.getModelo());
        dto.setPrecio(vehiculo.getPrecio());
        dto.setStock(vehiculo.getStock());
        dto.setTipo(vehiculo.getTipo());
       List<ImagenDTO> imagenesDTO = Optional.ofNullable(vehiculo.getImagenes())
               .orElse(Collections.emptyList())
               .stream()
               .map(imagen -> new ImagenDTO(imagen.getId(), imagen.getNombre()))
               .collect(Collectors.toList());

       dto.setImagenes(imagenesDTO);
       return dto;
    }
}
