package com.example.ecommercezapatillas.repositories;

import com.example.ecommercezapatillas.entities.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {
}
