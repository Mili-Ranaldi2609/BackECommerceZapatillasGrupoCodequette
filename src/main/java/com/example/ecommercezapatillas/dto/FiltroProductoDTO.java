package com.example.ecommercezapatillas.dto;

import com.example.ecommercezapatillas.entities.enums.Color;
import com.example.ecommercezapatillas.entities.enums.Sexo;
import com.example.ecommercezapatillas.entities.enums.Talle;
import lombok.Data;

import java.util.List;

@Data
public class FiltroProductoDTO {
    private String descripcion;
    private Sexo sexo;
    private String tipoProducto;
    private List<Long> categoriasIds;
    private Color color;
    private Talle talle;
    private String marca;
    private Double precioMin;
    private Double precioMax;
}
