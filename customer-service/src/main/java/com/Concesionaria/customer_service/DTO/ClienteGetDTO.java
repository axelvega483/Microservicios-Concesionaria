package com.Concesionaria.customer_service.DTO;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClienteGetDTO {
    private Integer id;
    private String nombre;
    private String email;
    private String dni;
    private Boolean activo;
    private List<ClienteVentaDTO> ventas;
    private String throwable;
}