package com.Concesionaria.customer_service.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClientePostDTO {
    @NotNull
    private String nombre;
    @NotNull
    private String email;
    @NotNull
    private String dni;
    private List<Integer> ventasId;
}
