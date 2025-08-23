package com.Concesionaria.customer_service.service;

import com.Concesionaria.customer_service.DTO.*;
import com.Concesionaria.customer_service.model.Cliente;
import com.Concesionaria.customer_service.repository.ClienteRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (post.getVentasId() != null && !post.getVentasId().isEmpty()) {
            ventas = venta.obtenerVentasPorIds(post.getVentasId());
            if (ventas.size() != post.getVentasId().size()) {
                throw new EntityNotFoundException("Una o más ventas no existen");
            }
        }
        Cliente cliente = new Cliente();
        cliente.setActivo(Boolean.TRUE);
        cliente.setDni(post.getDni());
        cliente.setEmail(post.getEmail());
        cliente.setNombre(post.getNombre());
        Cliente saved = repo.save(cliente);
        ClienteGetDTO dto = MapperDTO.toDTO(saved);
        dto.setVentas(ventas);

        return dto;
    }

    @Override
    public Cliente save(Cliente cliente) {
        cliente.setActivo(Boolean.TRUE);
        return repo.save(cliente);
    }

    @Override
    public ClienteGetDTO actualizar(Integer id, ClientePutDTO put) {
        Cliente cliente = findById(id).orElse(null);
        if (cliente == null) {
            throw new EntityExistsException("El Cliente no existe");
        }
        List<ClienteVentaDTO> ventas = Collections.emptyList();
        if (put.getVentasId() != null && !put.getVentasId().isEmpty()) {
            ventas = venta.obtenerVentasPorIds(put.getVentasId());
            if (ventas.size() != put.getVentasId().size()) {
                throw new EntityNotFoundException("Una o más ventas no existen");
            }
        }
        cliente.setActivo(Boolean.TRUE);
        cliente.setDni(put.getDni());
        cliente.setEmail(put.getEmail());
        cliente.setNombre(put.getNombre());
        Cliente saved = repo.save(cliente);
        ClienteGetDTO dto = MapperDTO.toDTO(saved);
        dto.setVentas(ventas);
        return dto;
    }

    @Override
    public Optional<Cliente> findById(Integer id) {
        return repo.findById(id).filter(Cliente::getActivo);
    }

    @Override
    public List<Cliente> findAll() {
        return repo.findAll();
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
