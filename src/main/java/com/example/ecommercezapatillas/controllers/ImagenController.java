package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.entities.Imagen;
import com.example.ecommercezapatillas.services.ImagenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/imagen")
public class ImagenController extends BaseController<Imagen, Long> {

    private final ImagenService imagenService;

    @Autowired
    public ImagenController(ImagenService imagenService) {
        super(imagenService);
        this.imagenService = imagenService;
    }
}

