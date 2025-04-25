package com.example.ecommercezapatillas.repositories;

import com.example.ecommercezapatillas.entities.Admin;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends BaseRepository<Admin, Long> {

    // Buscar un admin por su imagen (por el ID de la imagen asociada)
    Admin findByImagenUserId(Long idImagen);

    // Si querés buscar por Auth0, username u otro dato heredado:
    Admin findByUserName(String userName);
}
