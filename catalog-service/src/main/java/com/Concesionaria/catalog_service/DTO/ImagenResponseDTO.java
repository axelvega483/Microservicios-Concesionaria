package com.Concesionaria.catalog_service.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImagenResponseDTO {
    private Integer id;
    private String nombre;
    private String url;
}