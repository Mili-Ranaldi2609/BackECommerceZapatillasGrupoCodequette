package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.entities.Detalle;
import com.example.ecommercezapatillas.services.DetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/detalles")
public class DetalleController extends BaseController<Detalle, Long> {

    private final DetalleService detalleService;

    @Autowired
    public DetalleController(DetalleService detalleService) {
        super(detalleService);
        this.detalleService = detalleService;
    }
}

