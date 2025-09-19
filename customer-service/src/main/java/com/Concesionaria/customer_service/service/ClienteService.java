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
        return repo.findByDniAndActivo(dni).isPresent();
    }

    @Override
    public ClienteGetDTO create(ClientePostDTO post) {
        if (existe(post.getDni())) {
            throw new EntityExistsException("El Cliente ya existe");
        }
        Cliente cliente = mapper.create(post);
        Cliente saved = repo.save(cliente);
        return mapper.toDTO(saved);
    }


    @Override
    public ClienteGetDTO actualizar(Integer id, ClientePutDTO put) {
        Cliente cliente = repo.findById(id).orElse(null);
        if (cliente == null) {
            throw new EntityExistsException("El Cliente no existe");
        }
        cliente = mapper.update(cliente, put);
        Cliente saved = repo.save(cliente);
        return mapper.toDTO(saved);
    }

    @Override
    @CircuitBreaker(name = "sales-service", fallbackMethod = "findByClienteNoVenta")
    @Retry(name = "sales-service")
    public Optional<ClienteGetDTO> findById(Integer id) {
        Optional<Cliente> optUser = repo.findById(id).filter(Cliente::getActivo);
        if (optUser.isPresent()) {
            ClienteGetDTO dto = mapper.toDTO(optUser.get());
            List<ClienteVentaDTO> ventas = venta.obtenerVentasPorCliente(dto.getId());
            dto.setVentas(ventas);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    public Optional<ClienteGetDTO> findByClienteNoVenta(Integer id, Throwable throwable) {
        System.err.println("Fallback ejecutado para findByClienteNoVenta(): " + throwable.getMessage());
        Optional<Cliente> optUser = repo.findById(id).filter(Cliente::getActivo);
        if (optUser.isPresent()) {
            ClienteGetDTO dto = mapper.toDTO(optUser.get());
            dto.setVentas(Collections.emptyList());
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    @CircuitBreaker(name = "sales-service", fallbackMethod = "findByAllClientenoVenta")
    @Retry(name = "sales-service")
    public List<ClienteGetDTO> findAll() {
        List<Cliente> clientes = repo.findAll();
        List<ClienteGetDTO> dtos = new ArrayList<>();
        for (Cliente cliente : clientes) {
            ClienteGetDTO dto = mapper.toDTO(cliente);
            List<ClienteVentaDTO> ventas = venta.obtenerVentasPorCliente(cliente.getId());
            dto.setVentas(ventas);
            dtos.add(dto);
        }
        return dtos;
    }

    public List<ClienteGetDTO> findByAllClientenoVenta(Throwable throwable) {
        System.err.println("Fallback ejecutado para findByAllClientenoVenta(): " + throwable.getMessage());
        List<Cliente> clientes = repo.findAll();
        List<ClienteGetDTO> dtos = new ArrayList<>();
        for (Cliente cliente : clientes) {
            ClienteGetDTO dto = mapper.toDTO(cliente);
            dto.setVentas(Collections.emptyList());
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
