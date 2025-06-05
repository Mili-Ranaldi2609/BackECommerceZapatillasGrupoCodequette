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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productoid") 
    private Producto producto;
   
    @OneToOne(cascade = CascadeType.ALL) 
    @JoinColumn(name = "precio_id")
    @JsonIgnore
    private Precio precio;

    @OneToMany(mappedBy = "detalle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Imagen> imagenes = new ArrayList<>(); 

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
