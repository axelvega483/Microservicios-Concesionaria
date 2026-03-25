package com.Concesionaria.catalog_service.repository;

import com.Concesionaria.catalog_service.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {

    Optional<Vehiculo> findByMarcaAndModeloAndAnioModeloAndActivoTrue(String marca, String modelo, Integer anioModelo);

    List<Vehiculo> findByActivoTrue();
}
