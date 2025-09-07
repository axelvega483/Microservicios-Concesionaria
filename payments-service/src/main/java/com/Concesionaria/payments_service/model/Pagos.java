package com.Concesionaria.payments_service.model;

import com.Concesionaria.payments_service.util.EstadoPagos;
import com.Concesionaria.payments_service.util.MetodoPago;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pagos")
public class Pagos implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    private LocalDate fechaPago;

    @NotNull(message = "El metodo de pago no puede estar vacía")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    @NotNull(message = "El monto no puede estar vacía")
    @Column(nullable = false)
    private BigDecimal monto;

    @NotNull(message = "Los pagos debe estar registrada en una venta")
    private Integer ventaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPagos estado;

    @Column(nullable = false)
    private Boolean activo = true;

}
