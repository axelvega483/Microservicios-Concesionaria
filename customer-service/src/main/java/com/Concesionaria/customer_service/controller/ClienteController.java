package com.Concesionaria.customer_service.controller;

import com.Concesionaria.customer_service.DTO.ClienteGetDTO;
import com.Concesionaria.customer_service.DTO.ClientePostDTO;
import com.Concesionaria.customer_service.DTO.ClientePutDTO;
import com.Concesionaria.customer_service.service.IClienteService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("customer")
public class ClienteController {

    @Autowired
    private IClienteService clienteService;


    @PostMapping("crear")
    public ResponseEntity<?> crear(
            @Valid @RequestBody ClientePostDTO postDTO) {
        ClienteGetDTO dto = clienteService.create(postDTO);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<?> listarUsuario() {
        List<ClienteGetDTO> dto = clienteService.findAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @GetMapping("{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Integer id) {
        ClienteGetDTO cliente = clienteService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }


    @PutMapping("{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody ClientePutDTO putDTO) {
        ClienteGetDTO dto = clienteService.actualizar(id, putDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        ClienteGetDTO cliente = clienteService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        ClienteGetDTO dto = clienteService.delete(cliente.id());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}