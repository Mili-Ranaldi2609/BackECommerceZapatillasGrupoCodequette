package com.example.ecommercezapatillas.repositories;

import com.example.ecommercezapatillas.entities.Producto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends BaseRepository<Producto,Long> {
    default Producto findAllByImagenesProductosAndId(Long idArticulo) {
        return null;
    }
    List<Producto> findByDenominacionContainingIgnoreCase(String keyword);

}
