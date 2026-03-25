package com.Concesionaria.customer_service.DTO;

import com.Concesionaria.customer_service.model.Cliente;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MapperDTO {
    public ClienteGetDTO toDTO(Cliente cliente, List<ClienteVentaDTO> ventas) {
        return new ClienteGetDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getEmail(),
                cliente.getDni(),
                cliente.isActivo(),
                ventas

        );

    }

    public ClienteGetDTO toDTO(Cliente cliente) {
        return new ClienteGetDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getEmail(),
                cliente.getDni(),
                cliente.isActivo(),
                Collections.emptyList()

        );

    }

    public Cliente toEntity(ClientePostDTO post) {
        return Cliente.builder()
                .dni(post.dni())
                .email(post.email())
                .nombre(post.nombre())
                .activo(Boolean.TRUE)
                .build();
    }

    public void fromUpdateDTO(Cliente cliente, ClientePutDTO put) {
        if (put.dni() != null) cliente.setDni(put.dni());
        if (put.email() != null) cliente.setEmail(put.email());
        if (put.nombre() != null) cliente.setNombre(put.nombre());
        if(put.activo()!=null) cliente.setActivo(put.activo());
    }
    public List<ClienteGetDTO> toDTOList(List<Cliente> clientes) {
        return clientes.stream().filter(Cliente::isActivo).map(this::toDTO).toList();
    }
}
