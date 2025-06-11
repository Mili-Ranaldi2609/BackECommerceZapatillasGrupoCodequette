package com.example.ecommercezapatillas.controllers;
import com.example.ecommercezapatillas.entities.Localidad;
import com.example.ecommercezapatillas.entities.Provincia;
import com.example.ecommercezapatillas.services.LocalidadService; // Necesitarás un servicio de Localidad
import com.example.ecommercezapatillas.services.ProvinciaService; // Necesitarás un servicio de Provincia

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/ubicaciones") 
@AllArgsConstructor 
public class UbicacionesController {

    private final ProvinciaService provinciaService;
    private final LocalidadService localidadService;

    @GetMapping("/provincias")
    public ResponseEntity<List<Provincia>> getAllProvincias() {
        try {
            List<Provincia> provincias = provinciaService.listar(); 
            return ResponseEntity.ok(provincias);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener provincias.", e);
        }
    }

    @GetMapping("/localidades")
    public ResponseEntity<List<Localidad>> getLocalidadesByProvincia(@RequestParam Long provinciaId) {
        if (provinciaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere el parámetro 'provinciaId'.");
        }
        try {
            List<Localidad> localidades = localidadService.listarPorProvincia(provinciaId);
            return ResponseEntity.ok(localidades);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al obtener localidades por provincia.", e);
        }
    }
}
