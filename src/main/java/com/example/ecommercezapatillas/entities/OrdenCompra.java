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
    @Column(name = "estado_pago")
    private String estadoPago;
    @Column(name = "mp_external_reference", unique = true)
    private String mpExternalReference;
    @Column(name = "mp_payment_id")
    private Long mpPaymentId;
    @ManyToOne
    @JoinColumn(name = "id_usuario_direccion")
    private Direccion direccion;
    @ManyToOne
    @JoinColumn(name = "id_usuario_comprador")
    private User usuarioComprador;
    @Builder.Default
    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<OrdenCompraDetalle> detalles = new HashSet<>();

}
