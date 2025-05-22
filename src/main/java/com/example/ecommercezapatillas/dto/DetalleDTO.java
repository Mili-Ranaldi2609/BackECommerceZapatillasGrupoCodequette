package com.example.ecommercezapatillas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetalleDTO {
    private String color;
    private String talle;
    private String marca;
    private Integer stock;
    private Double precio;
}
