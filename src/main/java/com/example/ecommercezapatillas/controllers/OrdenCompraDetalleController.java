package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.entities.OrdenCompraDetalle;
import com.example.ecommercezapatillas.services.OrdenCompraDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orden_compra_detalle")
public class OrdenCompraDetalleController extends BaseController<OrdenCompraDetalle, Long> {

    private final OrdenCompraDetalleService ordenCompraDetalleService;

    @Autowired
    public OrdenCompraDetalleController(OrdenCompraDetalleService ordenCompraDetalleService) {
        super(ordenCompraDetalleService);
        this.ordenCompraDetalleService = ordenCompraDetalleService;
    }
}
