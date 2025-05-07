package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.dto.CategoriaMenuDTO;
import com.example.ecommercezapatillas.entities.enums.Sexo;
import com.example.ecommercezapatillas.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@CrossOrigin(origins = "*") // opcional para frontend
public class MenuController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaMenuDTO>> getMenuBySexo(@RequestParam Sexo sexo) {
        return ResponseEntity.ok(categoriaService.obtenerMenuPorSexo(sexo));
    }
}
