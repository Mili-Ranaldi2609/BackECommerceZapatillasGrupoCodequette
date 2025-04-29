package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.entities.Provincia;
import com.example.ecommercezapatillas.services.ProvinciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/provincia")

public class ProvinciaController extends BaseController<Provincia, Long> {

    private final ProvinciaService provinciaService;

    @Autowired
    public ProvinciaController(ProvinciaService provinciaService) {
        super(provinciaService);
        this.provinciaService = provinciaService;
    }
}

