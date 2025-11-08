package com.Concesionaria.auth_service.controller;

import com.Concesionaria.auth_service.DTO.*;
import com.Concesionaria.auth_service.model.User;
import com.Concesionaria.auth_service.service.IUserServicie;
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
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("auth")
@Tag(name = "Autenticación", description = "Controlador para operaciones de autenticación y gestión de usuarios")
public class authController {

    @Autowired
    private IUserServicie userService;

    @Autowired
    private MapperDto mapper;

    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflicto - el usuario ya existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("crear")
    public ResponseEntity<UserGetDTO> crear(
            @Parameter(description = "Datos del usuario a crear", required = true)
            @Valid @RequestBody UserPostDTO postDTO) {
        UserGetDTO dto = userService.crear(postDTO);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario con correo y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<UserGetDTO> login(
            @Parameter(description = "Credenciales de acceso", required = true)
            @RequestBody LoginDTO loginDTO) {
        Optional<User> optionalUsuario = userService.findByCorreoAndPassword(loginDTO.getEmail(), loginDTO.getPassword());

        User user = optionalUsuario.orElseThrow(() -> new SecurityException("Credenciales no encontradas"));
        UserGetDTO dto = mapper.toDTO(user);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Listar todos los usuarios", description = "Devuelve una lista con todos los usuarios registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios listados correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<UserGetDTO>> listarUsuario() {
        List<UserGetDTO> dto = userService.findAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Devuelve un usuario específico basado en su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("{id}")
    public ResponseEntity<UserGetDTO> obtenerUsuario(
            @Parameter(description = "ID del usuario a buscar", example = "1", required = true)
            @PathVariable Integer id) {
        UserGetDTO usuario = userService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @Operation(summary = "Actualizar usuario existente", description = "Actualiza la información de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto en la actualización"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("{id}")
    public ResponseEntity<UserGetDTO> actualizar(
            @Parameter(description = "ID del usuario a actualizar", example = "1", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Datos actualizados del usuario", required = true)
            @RequestBody UserPutDTO putDTO) {
        UserGetDTO dto = userService.actualizar(id, putDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<UserGetDTO> eliminar(
            @Parameter(description = "ID del usuario a eliminar", example = "1", required = true)
            @PathVariable Integer id) {
        UserGetDTO user = userService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        userService.delete(user.getId());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}