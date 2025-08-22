package com.Concesionaria.auth_service.controller;

import com.Concesionaria.auth_service.DTO.*;
import com.Concesionaria.auth_service.model.User;
import com.Concesionaria.auth_service.service.IUserServicie;
import com.Concesionaria.auth_service.util.ApiResponse;
import jakarta.persistence.EntityExistsException;
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
public class authController {
    @Autowired
    private IUserServicie userService;


    @PostMapping("crear")
    public ResponseEntity<?> crear(@Valid @RequestBody UserPostDTO postDTO) {
        try {
            UserGetDTO dto = userService.crear(postDTO);
            return new ResponseEntity<>(new ApiResponse<>("Usuario Creado", dto, true), HttpStatus.CREATED);
        } catch (EntityExistsException e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            Optional<User> optionalUsuario = userService.findByCorreoAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
            System.out.println("user" + optionalUsuario.toString());
            if (optionalUsuario.isPresent()) {
                UserGetDTO dto = MapperDto.toDTO(optionalUsuario.get());
                return new ResponseEntity<>(new ApiResponse<>("Login correcto", dto, true), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiResponse<>("Credenciales no encontradas", null, false), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }


    @GetMapping
    public ResponseEntity<?> listarUsuario() {
        try {
            List<UserGetDTO> dto = userService.findAll().stream().map(MapperDto::toDTO).toList();
            return new ResponseEntity<>(new ApiResponse<>("Listado de usuarios obtenidos correctamente", dto, true), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: ", null, false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Integer id) {
        try {
            User usuario = userService.findById(id).orElse(null);
            if (usuario != null) {
                UserGetDTO dto = MapperDto.toDTO(usuario);
                return new ResponseEntity<>(new ApiResponse<>("Usuario encontrado con éxito", dto, true), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse<>("Usuario no encontrado", null, false), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: ", null, false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody UserPutDTO putDTO) {
        try {
            UserGetDTO dto = userService.actualizar(id, putDTO);
            return new ResponseEntity<>(new ApiResponse<>("Usuario actualiazo", dto, true), HttpStatus.OK);
        } catch (EntityExistsException e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            User user = userService.findById(id).orElse(null);
            if(user !=null){
                userService.delete(user.getId());
                UserGetDTO dto = MapperDto.toDTO(user);
                return new ResponseEntity<>(new ApiResponse<>("Usuario dado de baja", dto, true), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiResponse<>("Usuario no encontrado: ", null, false), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>("Error: " + e.getMessage(), null, false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
