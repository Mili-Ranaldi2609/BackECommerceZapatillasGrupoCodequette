package com.example.ecommercezapatillas.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class OrdenCompraResponseDTO {
    private Long id; 
    private Float total; 
    private LocalDateTime fechaCompra;
    private String estado;
    private String externalReference; 
    private String customerName;
    private String shippingAddress;
    private List<OrdenCompraDetalleDTO> detalles;
}