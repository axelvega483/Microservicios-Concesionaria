package com.Concesionaria.auth_service.DTO;

import com.Concesionaria.auth_service.model.User;
import org.springframework.stereotype.Component;

@Component
public class MapperDto {
    public  UserGetDTO toDTO(User user) {
        UserGetDTO dto = new UserGetDTO();
        dto.setId(user.getId());
        dto.setDni(user.getDni());
        dto.setRol(user.getRol());
        dto.setEmail(user.getEmail());
        dto.setNombre(user.getNombre());
        dto.setActivo(user.getActivo());
        return dto;
    }
    public User create (UserPostDTO post){
        User usuario = new User();
        usuario.setActivo(Boolean.TRUE);
        usuario.setDni(post.getDni());
        usuario.setEmail(post.getEmail());
        usuario.setNombre(post.getNombre());
        usuario.setPassword(post.getPassword());
        usuario.setRol(post.getRol());
        return usuario;
    }
    public User update(User user, UserPutDTO put) {
        if (put.getActivo() != null) user.setActivo(put.getActivo());
        if (put.getDni() != null) user.setDni(put.getDni());
        if (put.getNombre() != null) user.setNombre(put.getNombre());
        if (put.getPassword() != null) user.setPassword(put.getPassword());
        if (put.getRol() != null) user.setRol(put.getRol());
        if (put.getEmail() != null) user.setEmail(put.getEmail());
        return user;
    }
}
