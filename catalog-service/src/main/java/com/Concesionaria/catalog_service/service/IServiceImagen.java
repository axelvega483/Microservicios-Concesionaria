package com.Concesionaria.catalog_service.service;

import com.Concesionaria.catalog_service.model.Imagen;
import com.Concesionaria.catalog_service.model.Vehiculo;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IServiceImagen {

    Imagen save(Imagen imagen);

    Optional<Imagen> findById(Integer id);

    List<Imagen> findAll();

    void delete(Integer id);

    List<Imagen> findByVehiculoId(Integer vehiculoId);

    void deleteByVehiculoId(Integer vehiculoId);

    List<Imagen> procesarImagenes(MultipartFile[] archivos, Vehiculo vehiculo);

    void eliminarArchivoImagen(String nombreArchivo);

    Resource obtenerArchivoImagen(String nombreArchivo) throws IOException;

    String obtenerExtensionArchivo(String nombreArchivo);
}
