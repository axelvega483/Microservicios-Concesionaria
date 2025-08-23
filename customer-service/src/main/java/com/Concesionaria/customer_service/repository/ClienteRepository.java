package com.Concesionaria.customer_service.repository;

import com.Concesionaria.customer_service.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    @Query("SELECT c FROM Cliente c WHERE c.activo=TRUE AND c.dni=:dni")
    Optional<Cliente> findByDniAndActivo(String dni);
}
