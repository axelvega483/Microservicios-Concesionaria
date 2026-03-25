package com.Concesionaria.auth_service.DTO;

import com.Concesionaria.auth_service.model.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Component
public class MapperDto {
    public  UserGetDTO toDTO(User user,List<UserVentaDTO> ventas) {
        return new UserGetDTO(
                user.getId(),
                user.getNombre(),
                user.getEmail(),
                user.getDni(),
                user.getRol(),
                user.isActivo(),
                ventas
        );
    }
    public UserGetDTO toDTO(User user) {
        return new UserGetDTO(
                user.getId(),
                user.getNombre(),
                user.getEmail(),
                user.getDni(),
                user.getRol(),
                user.isActivo(),
                Collections.emptyList()
        );
    }
    public User toEntity (UserPostDTO post){
        return  User.builder()
                .dni(post.dni())
                .email(post.email())
                .nombre(post.nombre())
                .password(post.password())
                .rol(post.rol())
                .activo(Boolean.TRUE)
                .build();
    }
    public void fromUpdateDTO(User user, UserPutDTO put) {
        if (put.dni() != null) user.setDni(put.dni());
        if (put.nombre() != null) user.setNombre(put.nombre());
        if (put.password() != null) user.setPassword(put.password());
        if (put.email() != null) user.setEmail(put.email());
    }
    public List<UserGetDTO> toDTOList(List<User> usuarios) {
        return usuarios.stream().filter(User::isActivo).map(this::toDTO).toList();
    }

}
