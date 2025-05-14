package com.example.ecommercezapatillas.entities;

import com.example.ecommercezapatillas.entities.enums.Color;
import com.example.ecommercezapatillas.entities.enums.Talle;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "detalles")
@Data
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
    private Producto producto;

    // 🔗 Relación con Precio (Muchos detalles pueden compartir un precio)
    @ManyToOne
    @JoinColumn(name = "precio_id")
    private Precio precio;

    @ManyToMany
    @JoinTable(
            name = "detalle_imagen",
            joinColumns = @JoinColumn(name = "detalle_id"),
            inverseJoinColumns = @JoinColumn(name = "imagen_id")
    )
    private List<Imagen> imagenes;
    @OneToMany(mappedBy = "detalle")
    private List<OrdenCompraDetalle> ordenes = new ArrayList<>();

}
