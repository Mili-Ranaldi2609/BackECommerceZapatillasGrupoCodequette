package com.example.ecommercezapatillas.services;


import com.example.ecommercezapatillas.entities.Provincia;
import com.example.ecommercezapatillas.repositories.ProvinciaRepository;
import org.springframework.stereotype.Service;

@Service
public class ProvinciaService extends BaseService<Provincia, Long> {
    public ProvinciaService(ProvinciaRepository provinciaRepository) {
        super(provinciaRepository);
    }

}
