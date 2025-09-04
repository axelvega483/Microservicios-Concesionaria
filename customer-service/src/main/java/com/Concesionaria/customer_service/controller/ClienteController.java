package com.Concesionaria.customer_service.controller;

import com.Concesionaria.customer_service.DTO.ClienteGetDTO;
import com.Concesionaria.customer_service.DTO.ClientePostDTO;
import com.Concesionaria.customer_service.DTO.ClientePutDTO;
import com.Concesionaria.customer_service.service.IClienteService;
import jakarta.persistence.EntityExistsException;
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
    public ResponseEntity<?> crear(@Valid @RequestBody ClientePostDTO postDTO) {
        try {
            ClienteGetDTO dto = clienteService.create(postDTO);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (EntityExistsException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarUsuario() {
        try {
            List<ClienteGetDTO> dto = clienteService.findAll();
            return new ResponseEntity<>(dto,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Integer id) {
        try {
            ClienteGetDTO cliente = clienteService.findById(id).orElse(null);
            if (cliente != null) {
                return new ResponseEntity<>(cliente, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Cliente no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error: ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody ClientePutDTO putDTO) {
        try {
            ClienteGetDTO dto = clienteService.actualizar(id, putDTO);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (EntityExistsException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            ClienteGetDTO cliente = clienteService.findById(id).orElse(null);
            if (cliente != null) {
                clienteService.delete(cliente.getId());
                return new ResponseEntity<>(cliente, HttpStatus.OK);
            }
            return new ResponseEntity<>("cliente no encontrado: ", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
