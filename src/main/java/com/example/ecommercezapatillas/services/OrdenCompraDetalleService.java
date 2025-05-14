package com.example.ecommercezapatillas.services;

import com.example.ecommercezapatillas.entities.OrdenCompraDetalle;
import com.example.ecommercezapatillas.repositories.OrdenCompraDetalleRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class OrdenCompraDetalleService extends BaseService<OrdenCompraDetalle, Long> {

    private final OrdenCompraDetalleRepository detalleRepository;

    public OrdenCompraDetalleService(OrdenCompraDetalleRepository detalleRepository) {
        super(detalleRepository);
        this.detalleRepository = detalleRepository;
    }

}