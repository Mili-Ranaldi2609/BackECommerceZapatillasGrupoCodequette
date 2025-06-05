package com.example.ecommercezapatillas.dto;

import com.example.ecommercezapatillas.entities.enums.Sexo;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String descripcion;
    private List<CategoriaDTO> categorias;
    private Sexo sexo;
    private String tipoProducto;
    private List<DetalleDTO> detalle;
    private boolean active;
}
