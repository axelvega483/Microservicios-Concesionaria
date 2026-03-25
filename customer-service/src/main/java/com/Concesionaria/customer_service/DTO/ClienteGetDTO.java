package com.Concesionaria.customer_service.DTO;



import java.util.List;

public record ClienteGetDTO(
         Integer id,
         String nombre,
         String email,
         String dni,
         boolean activo,
         List<ClienteVentaDTO> ventas) {

}