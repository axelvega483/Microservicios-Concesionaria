package com.Concesionaria.auth_service.DTO;

import com.Concesionaria.auth_service.util.RolUser;
import java.util.List;

public record UserGetDTO(
        Integer id,

         String nombre,

         String email,

         String dni,

         RolUser rol,

         boolean activo,

         List<UserVentaDTO> ventas) {

}
