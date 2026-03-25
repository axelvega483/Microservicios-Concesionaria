package com.Concesionaria.customer_service.service;

import com.Concesionaria.customer_service.DTO.*;
import com.Concesionaria.customer_service.model.Cliente;
import com.Concesionaria.customer_service.repository.ClienteRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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
    @Autowired
    private MapperDTO mapper;

    @Override
    public Boolean existe(String dni) {
        return repo.existsByDniAndActivoTrue(dni);
    }

    @Override
    public ClienteGetDTO create(ClientePostDTO post) {
        if (existe(post.dni())) {
            throw new EntityExistsException("El Cliente ya existe");
        }
        Cliente cliente = mapper.toEntity(post);
        Cliente saved = repo.save(cliente);
        return mapper.toDTO(saved);
    }


    @Override
    public ClienteGetDTO actualizar(Integer id, ClientePutDTO put) {
        Cliente cliente = repo.findById(id).orElse(null);
        if (cliente == null) {
            throw new EntityExistsException("El Cliente no existe");
        }
        mapper.fromUpdateDTO(cliente, put);
        Cliente saved = repo.save(cliente);
        return mapper.toDTO(saved);
    }

    @Override
    @CircuitBreaker(name = "sales-service", fallbackMethod = "findByClienteNoVenta")
    @Retry(name = "sales-service")
    public Optional<ClienteGetDTO> findById(Integer id) {
        Optional<Cliente> optUser = repo.findById(id).filter(Cliente::isActivo);
        if (optUser.isPresent()) {
            Cliente cliente = optUser.get();
            List<ClienteVentaDTO> ventas = venta.obtenerVentasPorCliente(cliente.getId());
            ClienteGetDTO dto = mapper.toDTO(cliente, ventas);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    public Optional<ClienteGetDTO> findByClienteNoVenta(Integer id, Throwable throwable) {
        System.err.println("Fallback ejecutado para findByClienteNoVenta(): " + throwable.getMessage());
        Optional<Cliente> optUser = repo.findById(id).filter(Cliente::isActivo);
        if (optUser.isPresent()) {
            Cliente cliente = optUser.get();
            ClienteGetDTO dto = mapper.toDTO(cliente);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public List<ClienteGetDTO> findAll() {
      return mapper.toDTOList(repo.findAll());
    }

    @Override
    public ClienteGetDTO delete(Integer id) {
        Optional<Cliente> clienteOptional = repo.findById(id);
        Cliente cliente = null;
        if (clienteOptional.isPresent()) {
            cliente = clienteOptional.get();
            cliente.setActivo(Boolean.FALSE);
            repo.save(cliente);
        }
        return mapper.toDTO(cliente);
    }
}
