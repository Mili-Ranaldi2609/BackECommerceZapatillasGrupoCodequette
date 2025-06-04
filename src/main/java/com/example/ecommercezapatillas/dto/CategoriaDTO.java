package com.example.ecommercezapatillas.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO  {
    private Long id;
    private String descripcion;
    private CategoriaDTO categoriaPadre;
    private List<CategoriaDTO> subcategorias;
    private List<ProductoDTO> productos;
};
