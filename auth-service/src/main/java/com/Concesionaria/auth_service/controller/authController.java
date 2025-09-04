package com.Concesionaria.auth_service.controller;

import com.Concesionaria.auth_service.DTO.*;
import com.Concesionaria.auth_service.model.User;
import com.Concesionaria.auth_service.service.IUserServicie;
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
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (EntityExistsException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            Optional<User> optionalUsuario = userService.findByCorreoAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
            System.out.println("user" + optionalUsuario.toString());
            if (optionalUsuario.isPresent()) {
                UserGetDTO dto = MapperDto.toDTO(optionalUsuario.get());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            }
            return new ResponseEntity<>("Credenciales no encontradas", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }


    @GetMapping
    public ResponseEntity<?> listarUsuario() {
        try {
            List<UserGetDTO> dto = userService.findAll();
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Integer id) {
        try {
            UserGetDTO usuario = userService.findById(id).orElse(null);
            if (usuario != null) {
                return new ResponseEntity<>(usuario, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error: "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody UserPutDTO putDTO) {
        try {
            UserGetDTO dto = userService.actualizar(id, putDTO);
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
            UserGetDTO user = userService.findById(id).orElse(null);
            if(user !=null){
                userService.delete(user.getId());
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
            return new ResponseEntity<>("Usuario no encontrado: ", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
