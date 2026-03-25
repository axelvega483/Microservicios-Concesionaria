package com.Concesionaria.auth_service.controller;

import com.Concesionaria.auth_service.DTO.UserGetDTO;
import com.Concesionaria.auth_service.DTO.UserPostDTO;
import com.Concesionaria.auth_service.DTO.UserPutDTO;
import com.Concesionaria.auth_service.DTO.UserRolDTO;
import com.Concesionaria.auth_service.service.IUserServicie;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("user")
public class UserController {

    @Autowired
    private IUserServicie userService;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("crear")
    public ResponseEntity<?> crear(
            @Valid @RequestBody UserPostDTO postDTO) {
        UserGetDTO dto = userService.crear(postDTO);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> listarUsuario() {
        List<UserGetDTO> dto = userService.findAll();
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN') or @usuarioSecurity.isCurrentUser(#id)")
    @GetMapping("{id}")
    public ResponseEntity<?> obtenerUsuario(
            @PathVariable Integer id) {
        UserGetDTO usuario = userService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User no encontrado"));
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN') or @usuarioSecurity.isCurrentUser(#id)")
    @PutMapping("{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer id,
            @RequestBody UserPutDTO putDTO) {
        UserGetDTO dto = userService.actualizar(id, putDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}/rol")
    public ResponseEntity<?> actualizarRolUsuario(@PathVariable Integer id, @RequestBody UserRolDTO usuarioDTO) {
        UserGetDTO dto = userService.actualizarRol(id, usuarioDTO);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Integer id) {
        UserGetDTO user = userService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User no encontrado"));
        userService.delete(user.id());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
