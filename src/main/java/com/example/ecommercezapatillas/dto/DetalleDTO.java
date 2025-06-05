package com.example.ecommercezapatillas.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DetalleDTO {
    private Long id;
    private String color;
    private String talle;
    private String marca;
    private Integer stock;
    private Double precioCompra;
    private Double precioVenta;
    private boolean active;
    private List<String> imagenes;
}
