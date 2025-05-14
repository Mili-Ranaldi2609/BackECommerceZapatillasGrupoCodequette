package com.example.ecommercezapatillas.services;

import com.example.ecommercezapatillas.entities.Detalle;
import com.example.ecommercezapatillas.repositories.DetalleRepository;
import org.springframework.stereotype.Service;

@Service
public class DetalleService extends BaseService<Detalle, Long>{
    private final DetalleRepository detalleRepository;

    public DetalleService(DetalleRepository detalleRepository) {
        super(detalleRepository);
        this.detalleRepository = detalleRepository;
    }

}
