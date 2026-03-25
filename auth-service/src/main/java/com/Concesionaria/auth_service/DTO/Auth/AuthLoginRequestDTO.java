package com.Concesionaria.auth_service.DTO.Auth;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequestDTO(@NotBlank String username, @NotBlank String password) {

}
