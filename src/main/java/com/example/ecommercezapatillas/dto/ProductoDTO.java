package com.example.ecommercezapatillas.dto;

import com.example.ecommercezapatillas.entities.enums.Sexo;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String denominacion;
    private Double precioOriginal;
    private Double precioFinal;
    private List<String> categorias;
    private Sexo sexo;
    private boolean tienePromocion;
    private List<String> imagenes;
    private DetalleDTO detalle;
}
