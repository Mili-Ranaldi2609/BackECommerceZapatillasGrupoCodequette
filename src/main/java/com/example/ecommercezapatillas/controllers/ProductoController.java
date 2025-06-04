package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.dto.ProductoDTO;
import com.example.ecommercezapatillas.entities.enums.Color;
import com.example.ecommercezapatillas.entities.enums.Sexo;
import com.example.ecommercezapatillas.entities.enums.Talle;
import com.example.ecommercezapatillas.entities.Producto;
import com.example.ecommercezapatillas.services.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final ObjectMapper objectMapper;
    @Autowired
     public ProductoController(ProductoService productoService, ObjectMapper objectMapper) {
        this.productoService = productoService;
        this.objectMapper = objectMapper;
    }
    // 🟢 Crear producto
    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(@RequestBody @Valid ProductoDTO dto) {
        ProductoDTO productoCreado = productoService.crearProducto(dto);
        return new ResponseEntity<>(productoCreado, HttpStatus.CREATED);
    }

    // 🟡 Editar producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> editarProducto(@PathVariable Long id,
                                                       @RequestBody @Valid ProductoDTO dto) {
        ProductoDTO productoActualizado = productoService.editarProducto(id, dto);
        return ResponseEntity.ok(productoActualizado);
    }
    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<ProductoDTO> productosDTO = productoService.listarProductosDTO();
            return ResponseEntity.ok(productosDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al obtener los productos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Long id) {
        try {
            Producto producto = productoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto == null) {
                return ResponseEntity.notFound().build();
            }
            ProductoDTO dto = productoService.convertirADTO(producto);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener el producto: " + e.getMessage());
        }
    }

    @GetMapping("/filtrar")
    public ResponseEntity<?> filtrarProductos(
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Sexo sexo,
            @RequestParam(required = false) String tipoProducto,
            @RequestParam(required = false) List<Long> categoriaIds,
            @RequestParam(required = false) Color color,
            @RequestParam(required = false) Talle talle,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax
    ) {
        try {
            List<ProductoDTO> productosDTO = productoService.filtrarProductosDTO(descripcion, sexo, tipoProducto, categoriaIds, color, talle, marca, precioMin, precioMax);
            return ResponseEntity.ok(productosDTO);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al filtrar productos: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> testProductoSimple() {
        try {
            Producto p = new Producto();
            p.setId(1L);
            p.setDescripcion("Zapatilla de prueba");
            p.setSexo(Sexo.UNISEX);
            p.setTipoProducto("calzado");

            return ResponseEntity.ok(p);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}