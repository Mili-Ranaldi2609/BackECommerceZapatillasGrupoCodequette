package com.example.ecommercezapatillas.services;

import com.example.ecommercezapatillas.entities.Usuario;
import com.example.ecommercezapatillas.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService extends BaseService<Usuario, Long> {
    public UsuarioService(UsuarioRepository usuarioRepository){
        super(usuarioRepository);
    }
}
