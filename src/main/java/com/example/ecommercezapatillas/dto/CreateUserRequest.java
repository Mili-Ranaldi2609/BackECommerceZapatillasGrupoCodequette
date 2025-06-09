// src/main/java/com/example/ecommercezapatillas/dto/CreateUserRequest.java
package com.example.ecommercezapatillas.dto;

import com.example.ecommercezapatillas.entities.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    private String firstname;
    private String lastname;
    private String username; // Este será el email
    private String password;
    private Rol role; // Opcional: para permitir que un admin cree otros admins/users
}