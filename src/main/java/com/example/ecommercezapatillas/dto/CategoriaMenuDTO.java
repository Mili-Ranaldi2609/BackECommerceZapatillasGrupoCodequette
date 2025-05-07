package com.example.ecommercezapatillas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoriaMenuDTO {
    private String categoria;
    private List<SubcategoriaDTO> subcategorias;

}
