package com.example.ecommercezapatillas.repositories;

import com.example.ecommercezapatillas.entities.Categoria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends BaseRepository<Categoria,Long> {
    List<Categoria> findByCategoriaPadreId(Long idPadre);
    List<Categoria> findByCategoriaPadreIsNull();

}
