package com.example.ecommercezapatillas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinciaResponseDTO {
    private Long id;
    private String nombre; // Asume que tu entidad Provincia tiene un método getNombre()
}