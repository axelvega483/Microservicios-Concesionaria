package com.Concesionaria.auth_service.model;

import com.Concesionaria.auth_service.util.RolUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras")
    @NotNull(message = "El nombre no puede estar vacio")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Email(message = "El email debe ser válido")
    @NotNull(message = "El email no puede estar vacío")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Size(min = 5, message = "La contraseña debe tener al menos 6 caracteres")
    @NotNull(message = "El password no puede estar vacio")
    @Column(name = "password", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Pattern(regexp = "\\d{7,8}", message = "El DNI debe tener entre 7 y 8 dígitos")
    @NotNull(message = "El dni no puede estar vacío")
    @Column(name = "dni", unique = true, nullable = false)
    private String dni;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El rol no puede estar vacío")
    @Column(name = "rol", nullable = false)
    private RolUser rol;

    @Column(nullable = false)
    private Boolean activo = true;

}
