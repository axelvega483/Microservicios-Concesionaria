package com.Concesionaria.customer_service.controller;

import com.Concesionaria.customer_service.DTO.ClienteGetDTO;
import com.Concesionaria.customer_service.DTO.ClientePostDTO;
import com.Concesionaria.customer_service.DTO.ClientePutDTO;
import com.Concesionaria.customer_service.service.IClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
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
@Tag(name = "Clientes", description = "Controlador para operaciones de clientes")
public class ClienteController {

    @Autowired
    private IClienteService clienteService;

    @Operation(summary = "Crear nuevo cliente", description = "Registra un nuevo cliente en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflicto - el cliente ya existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("crear")
    public ResponseEntity<ClienteGetDTO> crear(
            @Parameter(description = "Datos del cliente a crear", required = true)
            @Valid @RequestBody ClientePostDTO postDTO) {
        ClienteGetDTO dto = clienteService.create(postDTO);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos los clientes", description = "Devuelve una lista con todos los clientes registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes listados correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<ClienteGetDTO>> listarUsuario() {
        List<ClienteGetDTO> dto = clienteService.findAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Obtener cliente por ID", description = "Devuelve un cliente específico basado en su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("{id}")
    public ResponseEntity<ClienteGetDTO> obtenerUsuario(
            @Parameter(description = "ID del cliente a buscar", example = "1", required = true)
            @PathVariable Integer id) {
        ClienteGetDTO cliente = clienteService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar cliente existente", description = "Actualiza la información de un cliente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto en la actualización"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("{id}")
    public ResponseEntity<ClienteGetDTO> actualizar(
            @Parameter(description = "ID del cliente a actualizar", example = "1", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Datos actualizados del cliente", required = true)
            @RequestBody ClientePutDTO putDTO) {
        ClienteGetDTO dto = clienteService.actualizar(id, putDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<ClienteGetDTO> eliminar(
            @Parameter(description = "ID del cliente a eliminar", example = "1", required = true)
            @PathVariable Integer id) {
        ClienteGetDTO cliente = clienteService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        clienteService.delete(cliente.getId());
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }
}