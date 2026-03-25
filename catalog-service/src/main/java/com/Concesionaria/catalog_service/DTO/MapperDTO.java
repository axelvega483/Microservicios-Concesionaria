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
    public VehiculoGetDTO toDTO(Vehiculo vehiculo, List<VehiculoVentaDetalleDTO> detalleVentas) {
        List<ImagenDTO> imagenesDTO = Optional.ofNullable(vehiculo.getImagenes())
                .orElse(Collections.emptyList())
                .stream()
                .map(imagen -> new ImagenDTO(imagen.getId(), imagen.getNombre()))
                .collect(Collectors.toList());
        return new VehiculoGetDTO(
                vehiculo.getId(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getAnioModelo(),
                vehiculo.getPrecio(),
                vehiculo.getStock(),
                vehiculo.getColor(),
                vehiculo.getTipo(),
                vehiculo.getEstado(),
                vehiculo.getKilometraje(),
                imagenesDTO,
                vehiculo.isActivo(),
                detalleVentas
        );
    }

    public VehiculoGetDTO toDTO(Vehiculo vehiculo) {
        List<ImagenDTO> imagenesDTO = Optional.ofNullable(vehiculo.getImagenes())
                .orElse(Collections.emptyList())
                .stream()
                .map(imagen -> new ImagenDTO(imagen.getId(), imagen.getNombre()))
                .collect(Collectors.toList());
        return new VehiculoGetDTO(
                vehiculo.getId(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getAnioModelo(),
                vehiculo.getPrecio(),
                vehiculo.getStock(),
                vehiculo.getColor(),
                vehiculo.getTipo(),
                vehiculo.getEstado(),
                vehiculo.getKilometraje(),
                imagenesDTO,
                vehiculo.isActivo(),
                Collections.emptyList()
        );
    }

    public Vehiculo toEntity(VehiculoPostDTO post) {
        List<Imagen> imagenes = Optional.ofNullable(post.nombresImagenes())
                .orElse(Collections.emptyList())
                .stream()
                .map(nombre -> {
                    Imagen imagen = new Imagen();
                    imagen.setNombre(nombre);
                    return imagen;
                })
                .toList();

        return Vehiculo.builder()
                .color(post.color())
                .estado(post.estado())
                .kilometraje(post.kilometraje())
                .marca(post.marca())
                .modelo(post.modelo())
                .precio(post.precio())
                .stock(post.stock())
                .tipo(post.tipo())
                .activo(true)
                .anioModelo(post.anioModelo())
                .imagenes(imagenes)
                .build();
    }

    public void update(Vehiculo vehiculo, VehiculoPutDTO put) {
        if (put.anioModelo() != null) vehiculo.setAnioModelo(put.anioModelo());
        if (put.color() != null) vehiculo.setColor(put.color());
        if (put.estado() != null) vehiculo.setEstado(put.estado());
        if (put.kilometraje() != null) vehiculo.setKilometraje(put.kilometraje());
        if (put.marca() != null) vehiculo.setMarca(put.marca());
        if (put.modelo() != null) vehiculo.setModelo(put.modelo());
        if (put.precio() != null) vehiculo.setPrecio(put.precio());
        if (put.stock() != null) vehiculo.setStock(put.stock());
        if (put.tipo() != null) vehiculo.setTipo(put.tipo());
    }
    public List<VehiculoGetDTO> toDTOList(List<Vehiculo> vehiculos) {
        return vehiculos.stream().filter(Vehiculo::isActivo).map(this::toDTO).toList();
    }
}
