package com.Concesionaria.catalog_service.service;

import com.Concesionaria.catalog_service.DTO.VehiculoGetDTO;
import com.Concesionaria.catalog_service.DTO.VehiculoPostDTO;
import com.Concesionaria.catalog_service.DTO.VehiculoPutDTO;
import com.Concesionaria.catalog_service.model.Vehiculo;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IVehiculoService {
    VehiculoGetDTO createVehiculo(VehiculoPostDTO post);

    Vehiculo saveVehiculo(Vehiculo vehiculo);

    VehiculoGetDTO updateVehiculo (Integer id, VehiculoPutDTO put);

    Optional<VehiculoGetDTO> findByIdVehiculo(Integer id);

    List<VehiculoGetDTO> findAllVehiculo();

    Optional<Vehiculo> findEntityByIdVehiculo(Integer id);

    void deleteByIdVehiculo(Integer id);

    VehiculoGetDTO subirImagenesVehiculo(Integer vehiculoId, MultipartFile[] imagenes);

    ResponseEntity<Resource> obtenerImagen(Integer vehiculoId, Integer imagenId);

    VehiculoGetDTO eliminarImagenVehiculo(Integer vehiculoId, Integer imagenId);
}
