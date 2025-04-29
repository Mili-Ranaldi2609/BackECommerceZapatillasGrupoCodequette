package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.entities.Usuario;
import com.example.ecommercezapatillas.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController extends BaseController<Usuario,Long>{
    public UsuarioController(UsuarioService usuarioService){
        super(usuarioService);
    }
    @Autowired
    private UsuarioService usuarioService;
}
