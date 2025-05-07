package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.dto.CategoriaMenuDTO;
import com.example.ecommercezapatillas.entities.Categoria;
import com.example.ecommercezapatillas.entities.enums.Sexo;
import com.example.ecommercezapatillas.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")

public class CategoriaController extends BaseController<Categoria, Long> {

    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        super(categoriaService);
        this.categoriaService = categoriaService;
    }
    // 🔹 Obtener todas las categorías raíz (sin padre)
    @GetMapping("/raiz")
    public ResponseEntity<List<Categoria>> listarCategoriasRaiz() {
        List<Categoria> categoriasRaiz = categoriaService.listarCategoriasRaiz();
        return ResponseEntity.ok(categoriasRaiz);
    }

    // 🔹 Obtener subcategorías de una categoría padre
    @GetMapping("/{idPadre}/subcategorias")
    public ResponseEntity<List<Categoria>> listarSubcategorias(@PathVariable Long idPadre) {
        List<Categoria> subcategorias = categoriaService.listarSubcategorias(idPadre);
        return ResponseEntity.ok(subcategorias);
    }

}

