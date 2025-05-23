package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.entities.OrdenCompra;
import com.example.ecommercezapatillas.services.OrdenCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orden_compra")
public class OrdenCompraController extends BaseController<OrdenCompra, Long> {

    private final OrdenCompraService ordenCompraService;

    @Autowired
    public OrdenCompraController(OrdenCompraService ordenCompraService) {
        super(ordenCompraService);
        this.ordenCompraService = ordenCompraService;
    }



}