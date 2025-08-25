package com.Concesionaria.auth_service.DTO;

import com.Concesionaria.auth_service.util.RolUser;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserGetDTO {
    private Integer id;

    private String nombre;

    private String email;

    private String dni;

    private RolUser rol;

    private Boolean activo;

    private List<UserVentaDTO> ventas;

    private String throwable;
}
