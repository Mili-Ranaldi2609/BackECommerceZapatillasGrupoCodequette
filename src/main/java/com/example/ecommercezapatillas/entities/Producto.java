package com.example.ecommercezapatillas.entities;


import com.example.ecommercezapatillas.entities.enums.Sexo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "productos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Producto extends Base{

    @Column(length = 1000)
    private String descripcion;
    @Enumerated(EnumType.STRING)
    @Column(name = "sexo_producto")
    protected Sexo sexo;
    @Column(name = "tipo_producto")
    protected String tipoProducto;
    @ManyToMany
    @JoinTable(
            name = "producto_categoria",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    @JsonManagedReference
    private Set<Categoria> categorias = new HashSet<>();
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Detalle> detalles = new HashSet<>();


}

