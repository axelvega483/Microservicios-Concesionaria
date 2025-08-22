package com.Concesionaria.auth_service.service;

import com.Concesionaria.auth_service.DTO.UserGetDTO;
import com.Concesionaria.auth_service.DTO.UserPostDTO;
import com.Concesionaria.auth_service.DTO.UserPutDTO;
import com.Concesionaria.auth_service.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserServicie {
    public User save(User user);

    public Optional<User> findById(Integer id);

    public List<User> findAll();

    public void delete(Integer id);

    public UserGetDTO crear(UserPostDTO post);
    public UserGetDTO actualizar (Integer id, UserPutDTO put);

    public Boolean existe(String dni);

    public Optional<User> findByCorreoAndPassword(String email, String password);
}
