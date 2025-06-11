package com.example.ecommercezapatillas.repositories;

import com.example.ecommercezapatillas.entities.Direccion;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DireccionRepository extends BaseRepository<Direccion,Long> {
    List<Direccion> findAllByLocalidadId(Long idLocalidad);
     List<Direccion> findByUser_IdAndActive(Long userId, boolean active);
    List<Direccion> findByUser_Id(Long userId);
    Direccion findByIdAndUser_Id(Long direccionId, Long userId);

}