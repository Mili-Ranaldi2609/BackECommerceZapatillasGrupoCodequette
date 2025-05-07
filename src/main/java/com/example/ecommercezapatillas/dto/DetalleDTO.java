package com.example.ecommercezapatillas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class DetalleDTO {
    private Double precioCompra;
    private Integer stockActual;
    private Double cantidad;
    private Integer stockMaximo;
    private String color;
    private String talle;
}
