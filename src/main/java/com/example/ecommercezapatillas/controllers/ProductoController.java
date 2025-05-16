package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.entities.Producto;
import com.example.ecommercezapatillas.entities.enums.Color;
import com.example.ecommercezapatillas.entities.enums.Sexo;
import com.example.ecommercezapatillas.entities.enums.Talle;
import com.example.ecommercezapatillas.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController extends BaseController<Producto, Long> {

    private final ProductoService productoService;
    @Autowired
    public ProductoController(ProductoService productoService) {
        super(productoService);
        this.productoService = productoService;
    }
    @GetMapping("/filtrar")
    public ResponseEntity<List<Producto>> filtrarProductos(
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Sexo sexo,
            @RequestParam(required = false) String tipoProducto,
            @RequestParam(required = false) List<Long> categoriaIds,
            @RequestParam(required = false) Color color,
            @RequestParam(required = false) Talle talle,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax
    ) {
        List<Producto> productos = productoService.filtrarProductos(
                descripcion,
                sexo,
                tipoProducto,
                categoriaIds,
                color,
                talle,
                marca,
                precioMin,
                precioMax
        );
        return ResponseEntity.ok(productos);
    }
}
