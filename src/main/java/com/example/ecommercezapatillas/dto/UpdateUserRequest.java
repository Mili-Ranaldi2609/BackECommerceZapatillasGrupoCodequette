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
public class UpdateUserRequest {
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private Rol role;
    private boolean active;
    private String profileImage;
}
