    package com.example.ecommercezapatillas.repositories;

    import com.example.ecommercezapatillas.entities.Cliente;
    import org.springframework.stereotype.Repository;

    @Repository
    public interface ClienteRepository extends BaseRepository<Cliente, Long> {
        Cliente findAllByUsuarioId(Long idUsuario);
        Cliente findAllByImagenPersonaId(Long idImagen);
    }
