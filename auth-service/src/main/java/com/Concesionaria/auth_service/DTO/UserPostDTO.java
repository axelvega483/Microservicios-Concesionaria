package com.Concesionaria.auth_service.DTO;

import com.Concesionaria.auth_service.util.RolUser;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPostDTO {

    @NotNull
    private String nombre;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String dni;

    @NotNull
    private RolUser rol;

}
