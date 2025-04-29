package com.example.ecommercezapatillas.entities;

import com.example.ecommercezapatillas.entities.enums.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("admin")
public class Admin extends Usuario {

    @Column(name = "rol")
    private Rol rol;
    @Column(name = "activo")
    private Boolean activo;
}

