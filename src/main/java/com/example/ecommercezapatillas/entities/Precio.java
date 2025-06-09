package com.example.ecommercezapatillas.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "precios")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Precio extends Base {

    @Column(name = "precio_compra", nullable = false)
    private double precioCompra;

    @Column(name = "precio_venta", nullable = false)
    private double precioVenta;
    @Builder.Default
    @OneToMany(mappedBy = "precio", cascade = CascadeType.ALL)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Set<Detalle> detalles = new HashSet<>();
    @ManyToMany(mappedBy = "precios")
    @JsonIgnore
    private List<Descuentos> descuentos;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Precio)) return false;
        return getId() != null && getId().equals(((Precio) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
