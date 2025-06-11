package com.example.ecommercezapatillas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalidadResponseDTO {
    private Long id;
    private String nombre; 
    private ProvinciaResponseDTO provincia;
}