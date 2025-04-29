package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.entities.Descuentos;
import com.example.ecommercezapatillas.services.DescuentosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/descuentos")

public class DescuentosController extends BaseController<Descuentos, Long> {

    private final DescuentosService descuentosService;

    @Autowired
    public DescuentosController(DescuentosService descuentosService) {
        super(descuentosService);
        this.descuentosService = descuentosService;
    }

    // Buscar descuento por ID de descuento
    @GetMapping("/{idDescuento}")
    public ResponseEntity<Descuentos> obtenerPorIdDescuento(@PathVariable Long idDescuento) {
        try {
            Optional<Descuentos> descuento = descuentosService.obtenerPorIdDescuento(idDescuento);
            return descuento.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}
