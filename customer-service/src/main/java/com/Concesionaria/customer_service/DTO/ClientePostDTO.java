package com.Concesionaria.customer_service.DTO;

import jakarta.validation.constraints.NotNull;


public record ClientePostDTO(
        @NotNull
        String nombre,
        @NotNull
        String email,
        @NotNull
        String dni) {

}
