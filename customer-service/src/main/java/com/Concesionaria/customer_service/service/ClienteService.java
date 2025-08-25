package com.Concesionaria.customer_service.service;

import com.Concesionaria.customer_service.DTO.*;
import com.Concesionaria.customer_service.model.Cliente;
import com.Concesionaria.customer_service.repository.ClienteRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
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
    @CircuitBreaker(name = "venta-service", fallbackMethod = "findByClienteNoVenta")
    @Retry(name = "venta-service")
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

    public Optional<ClienteGetDTO> findByClienteNoVenta(Integer id,Throwable throwable) {
        Optional<Cliente> optUser = repo.findById(id).filter(Cliente::getActivo);
        if (optUser.isPresent()) {
            ClienteGetDTO dto = MapperDTO.toDTO(optUser.get());
            dto.setVentas(Collections.emptyList());
            dto.setThrowable("Throwable activado - Error: "+throwable.getMessage());
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    @CircuitBreaker(name = "venta-service", fallbackMethod = "findByAllClientenoVenta")
    @Retry(name = "venta-service")
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
    public List<ClienteGetDTO>findByAllClientenoVenta(Throwable throwable) {
        List<Cliente> clientes = repo.findAll();
        List<ClienteGetDTO> dtos = new ArrayList<>();
        for (Cliente cliente : clientes) {
            ClienteGetDTO dto = MapperDTO.toDTO(cliente);
            dto.setVentas(Collections.emptyList());
            dto.setThrowable("Throwable activado - Error: "+throwable.getMessage());
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
