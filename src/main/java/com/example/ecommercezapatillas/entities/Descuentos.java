package com.example.ecommercezapatillas.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "descuentos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Descuentos extends Base {

    @Column(name = "porcentaje", nullable = false)
    private Double porcentaje;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_final", nullable = false)
    private LocalDateTime fechaFinal;

    //Relación N:M con Precio a través de tabla intermedia
    @ManyToMany
    @JoinTable(
            name = "precio_descuento",
            joinColumns = @JoinColumn(name = "descuento_id"),
            inverseJoinColumns = @JoinColumn(name = "precio_id")
    )
    @JsonIgnore
    private List<Precio> precios;
}
