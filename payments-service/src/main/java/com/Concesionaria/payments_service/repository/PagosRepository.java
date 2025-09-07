package com.Concesionaria.payments_service.repository;

import com.Concesionaria.payments_service.model.Pagos;
import com.Concesionaria.payments_service.util.EstadoPagos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagosRepository extends JpaRepository<Pagos, Integer> {
    List<Pagos> findByVentaId(Integer ventaId);

    List<Pagos> findByVentaIdIn(List<Integer> ventaIds);

    List<Pagos> findByVentaIdAndEstado(Integer ventaId, EstadoPagos estado);

    Integer countByVentaIdAndActivoTrue(Integer ventaId);

    Optional<Pagos> findFirstByVentaIdOrderByFechaPagoDesc(Integer ventaId);

}
