package com.Concesionaria.auth_service.DTO.Auth;

public record AuthResponseDTO(String username,
                              String mensaje,
                              String token,
                              boolean status) {
}
