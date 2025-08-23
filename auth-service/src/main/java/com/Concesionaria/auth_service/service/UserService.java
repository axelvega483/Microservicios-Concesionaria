package com.Concesionaria.auth_service.service;

import com.Concesionaria.auth_service.DTO.*;
import com.Concesionaria.auth_service.model.User;
import com.Concesionaria.auth_service.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    public Optional<User> findById(Integer id) {
        return repo.findById(id).filter(User::getActivo);
    }

    @Override
    public List<User> findAll() {
        return repo.findAll();
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
        List<UserVentaDTO> ventas = Collections.emptyList();
        if (post.getVentasId() != null && !post.getVentasId().isEmpty()) {
            ventas = venta.obtenerVentasPorIds(post.getVentasId());
            if (ventas.size() != post.getVentasId().size()) {
                throw new EntityNotFoundException("Una o más ventas no existen");
            }
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
        dto.setVentas(ventas);
        return dto;
    }

    @Override
    public UserGetDTO actualizar(Integer id, UserPutDTO put) {
        User user = findById(id).orElse(null);
        if (user == null) {
            throw new EntityExistsException("El Usuario no existe");
        }
        List<UserVentaDTO> ventas = Collections.emptyList();
        if (put.getVentasId() != null && !put.getVentasId().isEmpty()) {
            ventas = venta.obtenerVentasPorIds(put.getVentasId());
            if (ventas.size() != put.getVentasId().size()) {
                throw new EntityNotFoundException("Una o más ventas no existen");
            }
        }
        user.setActivo(put.getActivo());
        user.setDni(put.getDni());
        user.setNombre(put.getNombre());
        user.setPassword(put.getPassword());
        user.setRol(put.getRol());
        user.setEmail(put.getEmail());
        User save = repo.save(user);
        UserGetDTO dto = MapperDto.toDTO(save);
        dto.setVentas(ventas);
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
