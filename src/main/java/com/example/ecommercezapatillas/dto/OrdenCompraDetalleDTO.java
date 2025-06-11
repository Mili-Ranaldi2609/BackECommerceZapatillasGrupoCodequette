package com.example.ecommercezapatillas.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraDetalleDTO {
    private Long id;
    private Integer cantidad;
    private Double subtotal;
    private Long detalleId;
    private Long productoId;
    private String productoNombre;
    private String detalleColor;
    private String detalleTalle;

}