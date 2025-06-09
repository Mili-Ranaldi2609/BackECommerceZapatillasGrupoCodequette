package com.example.ecommercezapatillas.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name="categorias")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Categoria extends Base{
    @Column(length = 500)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "categoria_padre_id")
    @JsonIgnore
    private Categoria categoriaPadre;
    
    @Builder.Default
    @OneToMany(mappedBy = "categoriaPadre", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Categoria> subcategorias = new HashSet<>();
    @Builder.Default
    @ManyToMany(mappedBy = "categorias")
    @JsonBackReference
    private Set<Producto> productos = new HashSet<>();

}

