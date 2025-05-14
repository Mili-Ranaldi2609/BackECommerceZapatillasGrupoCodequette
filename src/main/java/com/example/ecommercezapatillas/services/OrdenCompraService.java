package com.example.ecommercezapatillas.services;

import com.example.ecommercezapatillas.entities.OrdenCompra;
import com.example.ecommercezapatillas.repositories.OrdenCompraRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdenCompraService extends BaseService<OrdenCompra, Long> {

    private final OrdenCompraRepository ordenCompraRepository;

    public OrdenCompraService(OrdenCompraRepository ordenCompraRepository) {
        super(ordenCompraRepository);
        this.ordenCompraRepository = ordenCompraRepository;
    }

}