package com.Concesionaria.catalog_service.repository;

import com.Concesionaria.catalog_service.DTO.VehiculoGetDTO;
import com.Concesionaria.catalog_service.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo,Integer> {

    @Query("SELECT v FROM Vehiculo v WHERE v.activo=TRUE")
    List<Vehiculo> findAllActivo();

    @Query("SELECT v FROM Vehiculo v WHERE v.activo=TRUE AND v.marca=:marca AND v.modelo=:modelo AND v.anioModelo=:anioModelo")
    Optional<Vehiculo> findByMarcaModeloAnioModelo(String marca, String modelo, Integer anioModelo);
}
