package com.example.ecommercezapatillas.services;

import com.example.ecommercezapatillas.entities.Imagen;
import com.example.ecommercezapatillas.repositories.ImagenRepository;
import org.springframework.stereotype.Service;

@Service
public class ImagenService extends BaseService<Imagen,Long> {
    public ImagenService(ImagenRepository imagenRepository){
        super(imagenRepository);
    }

}