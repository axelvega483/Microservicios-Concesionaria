package com.Concesionaria.customer_service.DTO;


public record ClientePutDTO(
        String nombre,
        String email,
        String dni,
        Boolean activo) {

}
