package com.example.ecommercezapatillas.services;

import com.example.ecommercezapatillas.entities.Descuentos;
import com.example.ecommercezapatillas.repositories.DescuentosRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class DescuentosService extends BaseService<Descuentos, Long> {

    private final DescuentosRepository descuentosRepository;

    public DescuentosService(DescuentosRepository descuentosRepository) {
        super(descuentosRepository);
        this.descuentosRepository = descuentosRepository;
    }


    // Método para obtener descuento por ID
    public Optional<Descuentos> obtenerPorIdDescuento(Long idDescuento) throws Exception {
        try {
            // Uso del método findById que ahora es el método estándar
            return descuentosRepository.findById(idDescuento);
        } catch (Exception e) {
            throw new Exception("No se pudo obtener el descuento por ID de descuento: " + e.getMessage());
        }
    }


}
