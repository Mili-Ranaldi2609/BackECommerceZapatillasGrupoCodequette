package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.dto.ProductoDTO;
import com.example.ecommercezapatillas.entities.Producto;
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

}
