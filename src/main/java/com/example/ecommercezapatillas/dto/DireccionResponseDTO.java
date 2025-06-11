package com.example.ecommercezapatillas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DireccionResponseDTO {
    private Long id;
    private String calle;
    private int numero;
    private String cp;
    private LocalidadResponseDTO localidad; 
    private boolean active;
}
