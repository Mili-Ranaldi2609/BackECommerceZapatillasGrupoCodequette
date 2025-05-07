package com.example.ecommercezapatillas.repositories;

import com.example.ecommercezapatillas.entities.OrdenCompra;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdenCompraRepository extends BaseRepository<OrdenCompra, Long> {
    //todas las ordenes con una fecha específica
    List<OrdenCompra> findAllByFechaCompra(LocalDateTime fecha);
}
