package com.example.ecommercezapatillas.repositories;

import com.example.ecommercezapatillas.entities.OrdenCompra;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdenCompraRepository extends BaseRepository<OrdenCompra, Long> {
    Optional<OrdenCompra> findByMpExternalReference(String mpExternalReference);
}
