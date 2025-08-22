package com.Concesionaria.auth_service.repository;

import com.Concesionaria.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    @Query("SELECT u FROM User u WHERE u.activo=TRUE AND u.dni=:dni")
    public Optional<User> findByDniAndActivo(String dni);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.password = :password AND u.activo = TRUE")
    Optional<User> findByCorreoAndPassword(@Param("email") String email, @Param("password") String password);

}
