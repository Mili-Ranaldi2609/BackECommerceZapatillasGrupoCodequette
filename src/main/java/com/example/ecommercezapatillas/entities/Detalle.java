package com.example.ecommercezapatillas.entities;

import com.example.ecommercezapatillas.entities.enums.Color;
import com.example.ecommercezapatillas.entities.enums.Talle;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "detalles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Detalle extends Base {

    @Enumerated(EnumType.STRING)
    private Color color;

    @Enumerated(EnumType.STRING)
    private Talle talle;

    private String marca;

    private Integer stock;

    private Boolean estado;

    // 🔗 Relación con Producto (Muchos detalles para un producto)
    @ManyToOne
    @JoinColumn(name = "productoid")
    @JsonBackReference
    private Producto producto;

    // En Detalle.java
    @OneToOne(cascade = CascadeType.ALL) // Un Detalle tiene UN Precio
    @JoinColumn(name = "precio_id")
    @JsonIgnore
    private Precio precio;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "detalle_imagen", joinColumns = @JoinColumn(name = "detalle_id"), inverseJoinColumns = @JoinColumn(name = "imagen_id"))
    @JsonManagedReference
    private List<Imagen> imagenes;
    @OneToMany(mappedBy = "detalle")
    @JsonIgnore
    private List<OrdenCompraDetalle> ordenes = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Detalle))
            return false;
        return getId() != null && getId().equals(((Detalle) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
