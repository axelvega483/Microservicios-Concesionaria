package com.Concesionaria.sales_service.DTO;

import com.Concesionaria.sales_service.util.FrecuenciaPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VentaPostDTO {
    @NotNull(message = "La frecuencia de pago es requerida")
    private FrecuenciaPago frecuenciaPago;

    @NotNull(message = "El cliente es requerido")
    @Positive(message = "El ID del cliente debe ser positivo")
    private Integer clienteId;

    @NotNull(message = "El empleado es requerido")
    @Positive(message = "El ID del empleado debe ser positivo")
    private Integer userId;

    @DecimalMin(value = "0.0", message = "La entrega no puede ser negativa")
    private Double entrega;

    @Min(value = 1, message = "Las cuotas deben ser al menos 1")
    private Integer cuotas;

    @NotNull(message = "Debe incluir detalles de venta")
    @Size(min = 1, message = "Debe haber al menos un detalle")
    private List<@Valid DetalleVentaPostDTO> detalleVentas;
}
