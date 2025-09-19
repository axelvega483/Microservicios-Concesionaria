package com.Concesionaria.auth_service.service;

import com.Concesionaria.auth_service.DTO.*;
import com.Concesionaria.auth_service.model.User;
import com.Concesionaria.auth_service.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements IUserServicie {
    @Autowired
    private UserRepository repo;

    @Autowired
    private VentaFeignClient venta;

    @Autowired
    private MapperDto mapper;

    @Override
    public User save(User user) {
        user.setActivo(Boolean.TRUE);
        return repo.save(user);
    }

    @Override
    @CircuitBreaker(name = "sales-service", fallbackMethod = "findByUserNoVenta")
    @Retry(name = "sales-service")
    public Optional<UserGetDTO> findById(Integer id) {
        Optional<User> optUser = repo.findById(id).filter(User::getActivo);
        if (optUser.isPresent()) {
            UserGetDTO dto = mapper.toDTO(optUser.get());
            List<UserVentaDTO> ventas = venta.obtenerVentasPorUser(dto.getId());
            dto.setVentas(ventas);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    public Optional<UserGetDTO> findByUserNoVenta(Integer id, Throwable throwable) {
        System.err.println("Fallback ejecutado para findByUserNoVenta(): " + throwable.getMessage());
        Optional<User> optUser = repo.findById(id).filter(User::getActivo);
        if (optUser.isPresent()) {
            UserGetDTO dto = mapper.toDTO(optUser.get());
            dto.setVentas(Collections.emptyList());
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    @CircuitBreaker(name = "sales-service", fallbackMethod = "findAllUserNoVenta")
    @Retry(name = "sales-service")
    public List<UserGetDTO> findAll() {
        List<User> usuarios = repo.findAll();
        List<UserGetDTO> dtos = new ArrayList<>();
        for (User user : usuarios) {
            UserGetDTO dto = mapper.toDTO(user);
            List<UserVentaDTO> ventas = venta.obtenerVentasPorUser(user.getId());
            dto.setVentas(ventas);
            dtos.add(dto);
        }
        return dtos;
    }

    public List<UserGetDTO> findAllUserNoVenta(Throwable throwable) {
        System.err.println("Fallback ejecutado para findAllUserNoVenta(): " + throwable.getMessage());
            List<User> usuarios = repo.findAll();
            List<UserGetDTO> dtos = new ArrayList<>();
            for (User user : usuarios) {
                UserGetDTO dto = mapper.toDTO(user);
                dto.setVentas(Collections.emptyList());
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
        User usuario = mapper.create(post);
        User saved = repo.save(usuario);
        return mapper.toDTO(saved);
    }

    @Override
    public UserGetDTO actualizar(Integer id, UserPutDTO put) {
        User user = repo.findById(id).orElse(null);
        if (user == null) {
            throw new EntityExistsException("El Usuario no existe");
        }
        user = mapper.update(user, put);
        User save = repo.save(user);
        return mapper.toDTO(save);
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
