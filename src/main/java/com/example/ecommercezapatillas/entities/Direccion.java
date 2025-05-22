package com.example.ecommercezapatillas.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="direcciones")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Direccion extends Base{
    @Column(name="calle")
    private String calle;
    @Column(name="numero")
    private int numero;
    @Column(name="cp")
    private int cp;
    @ManyToOne
    @JoinColumn(name="localidad_id")
    private Localidad localidad;
    @ManyToMany(mappedBy = "direcciones")
    @JsonIgnore
    private Set<User> users = new HashSet<>();
}
