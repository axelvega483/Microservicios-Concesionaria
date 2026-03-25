package com.Concesionaria.sales_service.DTO;

public record VehiculoDTO(
        Integer id,
        String marca,
        String modelo,
        Integer anio,
        Double precio,
        Integer stock
) {}