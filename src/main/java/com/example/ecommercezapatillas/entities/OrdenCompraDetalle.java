package com.example.ecommercezapatillas.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orden_compra_detalle")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdenCompraDetalle extends Base {

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double subtotal;

    // 🔗 Relación con OrdenCompra (muchos detalles por orden)
    @ManyToOne
    @JoinColumn(name = "orden_compra_id")
    private OrdenCompra ordenCompra;

    // 🔗 Relación con Detalle (producto específico)
    @ManyToOne
    @JoinColumn(name = "detalle_id")
    private Detalle detalle;
}
