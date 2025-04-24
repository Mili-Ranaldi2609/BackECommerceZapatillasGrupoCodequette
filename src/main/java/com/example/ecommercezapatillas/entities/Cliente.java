package com.example.ecommercezapatillas.entities;

import com.example.ecommercezapatillas.entities.enums.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cliente extends Usuario{

    @Column(name = "rol")
    private Rol rol;
    @ManyToMany(mappedBy = "clientes")
    private List<Direccion> direcciones= new ArrayList<>();
}