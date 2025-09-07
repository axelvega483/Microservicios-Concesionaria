package com.Concesionaria.customer_service.DTO;

import com.Concesionaria.customer_service.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class MapperDTO {
    public ClienteGetDTO toDTO(Cliente cliente) {
        ClienteGetDTO dto = new ClienteGetDTO();
        dto.setId(cliente.getId());
        dto.setActivo(cliente.getActivo());
        dto.setDni(cliente.getDni());
        dto.setEmail(cliente.getEmail());
        dto.setNombre(cliente.getNombre());
        return dto;
    }

    public Cliente create(ClientePostDTO post) {
        Cliente cliente = new Cliente();
        cliente.setActivo(Boolean.TRUE);
        cliente.setDni(post.getDni());
        cliente.setEmail(post.getEmail());
        cliente.setNombre(post.getNombre());
        return cliente;
    }

    public Cliente update(Cliente cliente, ClientePutDTO put) {
        if (put.getActivo() != null) cliente.setActivo(put.getActivo());
        if (put.getDni() != null) cliente.setDni(put.getDni());
        if (put.getEmail() != null) cliente.setEmail(put.getEmail());
        if (put.getNombre() != null) cliente.setNombre(put.getNombre());
        return cliente;
    }
}
