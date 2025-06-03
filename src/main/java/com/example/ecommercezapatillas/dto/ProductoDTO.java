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
    private List<String> categorias;
    private Sexo sexo;
    private String tipoProducto;
    private List<String> imagenes;
    private List<DetalleDTO> detalle;
}
