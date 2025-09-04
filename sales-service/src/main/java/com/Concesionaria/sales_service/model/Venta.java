package com.Concesionaria.sales_service.model;

import com.Concesionaria.sales_service.util.EstadoVenta;
import com.Concesionaria.sales_service.util.FrecuenciaPago;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "venta")
public class Venta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "La fecha de la venta no puede estar vacía")
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull(message = "El total no puede estar vacío")
    @PositiveOrZero(message = "El total debe ser mayor o igual a cero")
    @Column(nullable = false)
    private BigDecimal total;

    @NotNull(message = "La frecuencia de pago no puede estar vacía")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FrecuenciaPago frecuenciaPago;

    @NotNull(message = "Debe incluir al menos un detalle de venta")
    @Size(min = 1, message = "Debe haber al menos un producto en la venta")
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("venta")
    private List<DetalleVenta> detalleVentas = new ArrayList<>();

    @NotNull(message = "La venta debe estar asociada a un cliente")
    private Integer clienteId;

    @NotNull(message = "La venta debe ser registrada por un empleado")
    private Integer userId;

    @Column
    private Double entrega; // porcentaje

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoVenta estado;

    @Column
    private Integer cuotas;

    @Column(nullable = false)
    private Boolean activo = true;

    public void calcularTotal() {
        // Calcular total basado en detalles
        this.total = detalleVentas.stream()
                .map(detalle -> detalle.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Validar que la entrega no sea mayor al total
        if (this.entrega != null && this.entrega > this.total.doubleValue()) {
            throw new IllegalArgumentException(
                    String.format("La entrega ($%.2f) no puede ser mayor al total de la venta ($%.2f)",
                            this.entrega, this.total.doubleValue())
            );
        }
    }
    public void actualizarSaldo(BigDecimal montoPagado) {
        if (total.subtract(montoPagado).compareTo(BigDecimal.ZERO) <= 0) {
            this.estado = EstadoVenta.FINALIZADO;
        } else {
            this.estado = EstadoVenta.ACTIVO;
        }
    }

    public BigDecimal getSaldoRestante(BigDecimal montoPagado) {
        return total.subtract(montoPagado);
    }
}
