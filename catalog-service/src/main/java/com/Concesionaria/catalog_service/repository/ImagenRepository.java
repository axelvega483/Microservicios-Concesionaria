package com.Concesionaria.catalog_service.repository;

import com.Concesionaria.catalog_service.model.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen,Integer> {
    @Query("SELECT i FROM Imagen i WHERE i.vehiculo.id = :vehiculoId")
    List<Imagen> findByVehiculoId(@Param("vehiculoId") Integer vehiculoId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Imagen i WHERE i.vehiculo.id = :vehiculoId")
    void deleteByVehiculoId(@Param("vehiculoId") Integer vehiculoId);
}
