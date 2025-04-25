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
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario extends Base{
    @Column(name = "auth_id")
    private String auth0Id;
    @Column(name = "user_name")
    private String userName;
    private String nombre;
    private String apellido;
    protected String email;
    protected Number dni;
    @OneToOne
    @JoinColumn(name="imagen_id")
    protected Imagen imagenUser;

    @Column(name = "password")
    private String password;  // Almacena la contraseña encriptada


}
