package com.Concesionaria.catalog_service.model;


import com.Concesionaria.catalog_service.util.EstadoVehiculo;
import com.Concesionaria.catalog_service.util.TipoVehiculo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vehiculo")
public class Vehiculo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(min = 2, max = 100)
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s\\-.,()]+$")
    @Column(nullable = false)
    private String marca;

    @Size(max = 255)
    private String modelo;

    @Min(1900)
    @Max(2100)
    private Integer anioModelo;

    @DecimalMin(value = "0.01")
    @Column(nullable = false)
    private Double precio;

    @Min(0)
    @Column(nullable = false)
    private Integer stock;

    @Size(max = 50)
    private String color;

    @Enumerated(EnumType.STRING)
    private TipoVehiculo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVehiculo estado;

    private Integer kilometraje;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Imagen> imagenes = new ArrayList<>();


    public void addImagen(Imagen imagen) {
        imagen.setVehiculo(this);
        this.imagenes.add(imagen);
    }

}
