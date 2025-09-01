package com.Concesionaria.customer_service.service;

import com.Concesionaria.customer_service.DTO.ClienteGetDTO;
import com.Concesionaria.customer_service.DTO.ClientePostDTO;
import com.Concesionaria.customer_service.DTO.ClientePutDTO;
import com.Concesionaria.customer_service.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface IClienteService {
    Boolean existe(String dni);

    ClienteGetDTO create(ClientePostDTO post);

    Cliente save(Cliente cliente);

    ClienteGetDTO actualizar (Integer id, ClientePutDTO put);

    Optional<ClienteGetDTO> findById(Integer id);

    List<ClienteGetDTO> findAll();

    void delete(Integer id);
}
