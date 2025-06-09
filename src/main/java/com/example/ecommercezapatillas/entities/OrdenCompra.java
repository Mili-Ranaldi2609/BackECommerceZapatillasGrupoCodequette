package com.example.ecommercezapatillas.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orden_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompra extends Base {
    @Column(name = "total")
    private Float total;
    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;
    
    @ManyToOne
    @JoinColumn(name="id_usuario_direccion")
    private Direccion direccion;
    @Builder.Default
    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<OrdenCompraDetalle> detalles = new HashSet<>();


}
