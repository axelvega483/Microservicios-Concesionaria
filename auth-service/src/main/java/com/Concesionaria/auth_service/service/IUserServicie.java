package com.Concesionaria.auth_service.service;

import com.Concesionaria.auth_service.DTO.UserGetDTO;
import com.Concesionaria.auth_service.DTO.UserPostDTO;
import com.Concesionaria.auth_service.DTO.UserPutDTO;
import com.Concesionaria.auth_service.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserServicie {
    User save(User user);

    Optional<User> findById(Integer id);

    List<User> findAll();

    void delete(Integer id);

    UserGetDTO crear(UserPostDTO post);

    UserGetDTO actualizar(Integer id, UserPutDTO put);

    Boolean existe(String dni);

    Optional<User> findByCorreoAndPassword(String email, String password);
}
