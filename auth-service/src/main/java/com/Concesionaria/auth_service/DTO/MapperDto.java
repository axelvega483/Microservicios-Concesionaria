package com.Concesionaria.auth_service.DTO;

import com.Concesionaria.auth_service.model.User;

public class MapperDto {
    public static UserGetDTO toDTO(User user) {
        UserGetDTO dto = new UserGetDTO();
        dto.setId(user.getId());
        dto.setDni(user.getDni());
        dto.setRol(user.getRol());
        dto.setEmail(user.getEmail());
        dto.setNombre(user.getNombre());
        dto.setActivo(user.getActivo());
        return dto;
    }
}
