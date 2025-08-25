package com.Concesionaria.auth_service.service;

import com.Concesionaria.auth_service.DTO.*;
import com.Concesionaria.auth_service.model.User;
import com.Concesionaria.auth_service.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.Column;
import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserService implements IUserServicie {
    @Autowired
    private UserRepository repo;

    @Autowired
    private VentaFeignClient venta;

    @Override
    public User save(User user) {
        user.setActivo(Boolean.TRUE);
        return repo.save(user);
    }

    @Override
    @CircuitBreaker(name = "venta-service",fallbackMethod = "findByUserNoVenta")
    @Retry(name = "venta-service")
    public Optional<UserGetDTO> findById(Integer id) {
        Optional<User> optUser = repo.findById(id).filter(User::getActivo);
        if (optUser.isPresent()) {
            UserGetDTO dto = MapperDto.toDTO(optUser.get());
            List<UserVentaDTO> ventas = venta.obtenerVentasPorUser(dto.getId());
            dto.setVentas(ventas);
            return Optional.of(dto);
        }
        return Optional.empty();
    }
    public Optional<UserGetDTO> findByUserNoVenta(Integer id,Throwable throwable) {
        Optional<User> optUser = repo.findById(id).filter(User::getActivo);
        if (optUser.isPresent()) {
            UserGetDTO dto = MapperDto.toDTO(optUser.get());
            dto.setVentas(Collections.emptyList());
            dto.setThrowable("Throwable activado - Error: "+throwable.getMessage());
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    @CircuitBreaker(name = "vanta-service",fallbackMethod = "findAllUserNoVenta")
    @Retry(name = "venta-service")
    public List<UserGetDTO> findAll() {
        List<User> usuarios = repo.findAll();
        List<UserGetDTO> dtos = new ArrayList<>();
        for (User user : usuarios) {
            UserGetDTO dto = MapperDto.toDTO(user);
            List<UserVentaDTO> ventas = venta.obtenerVentasPorUser(user.getId());
            dto.setVentas(ventas);
            dtos.add(dto);
        }
        return dtos;
    }
    public List<UserGetDTO> findAllUserNoVenta(Throwable throwable) {
        List<User> usuarios = repo.findAll();
        List<UserGetDTO> dtos = new ArrayList<>();
        for (User user : usuarios) {
            UserGetDTO dto = MapperDto.toDTO(user);
            dto.setVentas(Collections.emptyList());
            dto.setThrowable("Throwable activado - Error: "+throwable.getMessage());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public void delete(Integer id) {
        Optional<User> userOptional = repo.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActivo(Boolean.FALSE);
            repo.save(user);
        }
    }

    @Override
    public UserGetDTO crear(UserPostDTO post) {
        if (existe(post.getDni())) {
            throw new EntityExistsException("El Usuario ya existe");
        }

        User usuario = new User();
        usuario.setActivo(Boolean.TRUE);
        usuario.setDni(post.getDni());
        usuario.setEmail(post.getEmail());
        usuario.setNombre(post.getNombre());
        usuario.setPassword(post.getPassword());
        usuario.setRol(post.getRol());
        User saved = repo.save(usuario);
        UserGetDTO dto = MapperDto.toDTO(saved);

        return dto;
    }

    @Override
    public UserGetDTO actualizar(Integer id, UserPutDTO put) {
        User user = repo.findById(id).orElse(null);
        if (user == null) {
            throw new EntityExistsException("El Usuario no existe");
        }
        user.setActivo(put.getActivo());
        user.setDni(put.getDni());
        user.setNombre(put.getNombre());
        user.setPassword(put.getPassword());
        user.setRol(put.getRol());
        user.setEmail(put.getEmail());
        User save = repo.save(user);
        UserGetDTO dto = MapperDto.toDTO(save);
        return dto;
    }

    @Override
    public Boolean existe(String dni) {
        return repo.findByDniAndActivo(dni).isPresent();
    }

    @Override
    public Optional<User> findByCorreoAndPassword(String email, String password) {
        return repo.findByCorreoAndPassword(email, password);
    }
}
