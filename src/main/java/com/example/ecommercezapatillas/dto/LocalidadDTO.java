package com.example.ecommercezapatillas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalidadDTO {
    private Long id;
    private String localidad;
    private ProvinciaDTO provincia; 
} 
