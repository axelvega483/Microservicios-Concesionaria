package com.Concesionaria.auth_service.DTO;

import com.Concesionaria.auth_service.util.RolUser;
import jakarta.validation.constraints.NotNull;

public record UserPostDTO(
        @NotNull
        String nombre,

        @NotNull
        String email,

        @NotNull
        String password,

        @NotNull
        String dni,

        @NotNull
        RolUser rol
) {


}
