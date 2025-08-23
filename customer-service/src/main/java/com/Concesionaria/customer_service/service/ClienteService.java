package com.Concesionaria.customer_service.service;

import com.Concesionaria.customer_service.DTO.*;
import com.Concesionaria.customer_service.model.Cliente;
import com.Concesionaria.customer_service.repository.ClienteRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService implements IClienteService {
    @Autowired
    private ClienteRepository repo;
    @Autowired
    private VentaFeignClient venta;

    @Override
    public Boolean existe(String dni) {
        return repo.findByDniAndActivo(dni).isPresent();
    }

    @Override
    public ClienteGetDTO created(ClientePostDTO post) {
        if (existe(post.getDni())) {
            throw new EntityExistsException("El Cliente ya existe");
        }
        List<ClienteVentaDTO> ventas = Collections.emptyList();
        Cliente cliente = new Cliente();
        cliente.setActivo(Boolean.TRUE);
        cliente.setDni(post.getDni());
        cliente.setEmail(post.getEmail());
        cliente.setNombre(post.getNombre());
        Cliente saved = repo.save(cliente);
        ClienteGetDTO dto = MapperDTO.toDTO(saved);

        return dto;
    }

    @Override
    public Cliente save(Cliente cliente) {
        cliente.setActivo(Boolean.TRUE);
        return repo.save(cliente);
    }

    @Override
    public ClienteGetDTO actualizar(Integer id, ClientePutDTO put) {
        Cliente cliente = repo.findById(id).orElse(null);
        if (cliente == null) {
            throw new EntityExistsException("El Cliente no existe");
        }
        cliente.setActivo(Boolean.TRUE);
        cliente.setDni(put.getDni());
        cliente.setEmail(put.getEmail());
        cliente.setNombre(put.getNombre());
        Cliente saved = repo.save(cliente);
        ClienteGetDTO dto = MapperDTO.toDTO(saved);
        return dto;
    }

    @Override
    public Optional<ClienteGetDTO> findById(Integer id) {
        Optional<Cliente> optUser = repo.findById(id).filter(Cliente::getActivo);
        if (optUser.isPresent()) {
            ClienteGetDTO dto = MapperDTO.toDTO(optUser.get());
            List<ClienteVentaDTO> ventas = venta.obtenerVentasPorCliente(dto.getId());
            dto.setVentas(ventas);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public List<ClienteGetDTO> findAll() {
        List<Cliente> clientes = repo.findAll();
        List<ClienteGetDTO> dtos = new ArrayList<>();
        for (Cliente cliente : clientes) {
            ClienteGetDTO dto = MapperDTO.toDTO(cliente);
            List<ClienteVentaDTO> ventas = venta.obtenerVentasPorCliente(cliente.getId());
            dto.setVentas(ventas);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public void delete(Integer id) {
        Optional<Cliente> clienteOptional = repo.findById(id);
        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            cliente.setActivo(Boolean.FALSE);
            repo.save(cliente);
        }
    }
}
