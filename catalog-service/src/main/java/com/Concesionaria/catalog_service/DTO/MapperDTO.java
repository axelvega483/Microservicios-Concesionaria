package com.Concesionaria.catalog_service.DTO;

import com.Concesionaria.catalog_service.model.Imagen;
import com.Concesionaria.catalog_service.model.Vehiculo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MapperDTO {
    public VehiculoGetDTO toDTO(Vehiculo vehiculo) {
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

    public Vehiculo create(VehiculoPostDTO post) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setActivo(post.getActivo());
        vehiculo.setAnioModelo(post.getAnioModelo());
        vehiculo.setColor(post.getColor());
        vehiculo.setEstado(post.getEstado());
        vehiculo.setKilometraje(post.getKilometraje());
        vehiculo.setMarca(post.getMarca());
        vehiculo.setModelo(post.getModelo());
        vehiculo.setPrecio(post.getPrecio());
        vehiculo.setStock(post.getStock());
        vehiculo.setTipo(post.getTipo());
        List<String> nombresImagenes = Optional.ofNullable(post.getNombresImagenes()).orElse(Collections.emptyList());
        for (String nombre : nombresImagenes) {
            Imagen imagen = new Imagen();
            imagen.setNombre(nombre);
            vehiculo.addImagen(imagen);
        }
        return vehiculo;
    }

    public Vehiculo update(Vehiculo vehiculo, VehiculoPutDTO put) {
        if (put.getActivo() != null) vehiculo.setActivo(put.getActivo());
        if (put.getAnioModelo() != null) vehiculo.setAnioModelo(put.getAnioModelo());
        if (put.getColor() != null) vehiculo.setColor(put.getColor());
        if (put.getEstado() != null) vehiculo.setEstado(put.getEstado());
        if (put.getKilometraje() != null) vehiculo.setKilometraje(put.getKilometraje());
        if (put.getMarca() != null) vehiculo.setMarca(put.getMarca());
        if (put.getModelo() != null) vehiculo.setModelo(put.getModelo());
        if (put.getPrecio() != null) vehiculo.setPrecio(put.getPrecio());
        if (put.getStock() != null) vehiculo.setStock(put.getStock());
        if (put.getTipo() != null) vehiculo.setTipo(put.getTipo());
        return vehiculo;
    }
}
