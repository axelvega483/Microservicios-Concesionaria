package com.Concesionaria.catalog_service.repository;

import com.Concesionaria.catalog_service.model.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen,Integer> {

    List<Imagen> findByVehiculoId(Integer vehiculoId);

    void deleteByVehiculoId(Integer vehiculoId);
}
