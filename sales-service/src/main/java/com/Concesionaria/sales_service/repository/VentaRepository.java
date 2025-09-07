package com.Concesionaria.sales_service.repository;

import com.Concesionaria.sales_service.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    List<Venta> findByActivoTrue();

    List<Venta> findByDetalleVentas_VehiculoId(Integer vehiculoId);

    List<Venta> findByUserId(Integer userId);

    List<Venta> findByClienteId(Integer clienteId);
}
