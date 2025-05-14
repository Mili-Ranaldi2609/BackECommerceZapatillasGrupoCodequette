package com.example.ecommercezapatillas.services;
import com.example.ecommercezapatillas.dto.DetalleDTO;
import com.example.ecommercezapatillas.dto.ProductoDTO;
import com.example.ecommercezapatillas.entities.Categoria;
import com.example.ecommercezapatillas.entities.Descuentos;
import com.example.ecommercezapatillas.entities.Producto;
import com.example.ecommercezapatillas.repositories.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService extends BaseService<Producto, Long> {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        super(productoRepository);
        this.productoRepository = productoRepository;
    }
}
