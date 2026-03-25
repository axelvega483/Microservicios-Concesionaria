package com.Concesionaria.auth_service.repository;

import com.Concesionaria.auth_service.model.User;
import com.Concesionaria.auth_service.util.RolUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByDniAndActivoTrue(String dni);

    Optional<User> findByEmail(String email);

    Integer countByRol(RolUser rol);
}
