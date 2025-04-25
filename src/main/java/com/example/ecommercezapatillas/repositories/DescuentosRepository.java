package com.example.ecommercezapatillas.repositories;

import com.example.ecommercezapatillas.entities.Descuentos;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescuentosRepository extends BaseRepository<Descuentos,Long> {
    List<Descuentos> findAllByDescuentoId(Long idDescuento);
    List<Descuentos>findAllByArticuloId(Long idProducto);
}
