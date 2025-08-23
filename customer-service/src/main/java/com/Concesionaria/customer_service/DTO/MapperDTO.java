package com.Concesionaria.customer_service.DTO;

import com.Concesionaria.customer_service.model.Cliente;


public class MapperDTO {
    public static ClienteGetDTO toDTO(Cliente cliente) {
        ClienteGetDTO dto = new ClienteGetDTO();
        dto.setId(cliente.getId());
        dto.setActivo(cliente.getActivo());
        dto.setDni(cliente.getDni());
        dto.setEmail(cliente.getEmail());
        dto.setNombre(cliente.getNombre());
        return dto;
    }
}
