package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.entities.Localidad;
import com.example.ecommercezapatillas.services.LocalidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/localidades")

public class LocalidadController extends BaseController<Localidad, Long> {

    private final LocalidadService localidadService;

    @Autowired
    public LocalidadController(LocalidadService localidadService) {
        super(localidadService);
        this.localidadService = localidadService;
    }

    @GetMapping("/provincia/{idProvincia}")
    public List<Localidad> listarPorProvincia(@PathVariable Long idProvincia) throws Exception {
        return localidadService.listarPorProvincia(idProvincia);
    }
}

