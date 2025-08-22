package com.Concesionaria.auth_service.DTO;

import com.Concesionaria.auth_service.util.RolUser;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserPutDTO {

    private String nombre;

    private String email;

    private String dni;

    private String password;

    private RolUser rol;

    private Boolean activo;

    private List<Integer> ventasId;

}
