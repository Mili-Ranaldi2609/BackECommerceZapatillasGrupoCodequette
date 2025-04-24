package com.example.ecommercezapatillas.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orden_compra_detalle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompraDetalle extends Base{
    @ManyToOne
    @JoinColumn(name = "orden_compra_id")
    private OrdenCompra ordenCompra;

    @ManyToOne
    @JoinColumn(name = "productodetalle_id")
    private ProductoDetalle productoDetalle;

    @Column(name = "cantidad")
    private Integer cantidad;
}
