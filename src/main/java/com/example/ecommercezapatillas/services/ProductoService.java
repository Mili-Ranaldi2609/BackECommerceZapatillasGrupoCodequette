package com.example.ecommercezapatillas.services;

import com.example.ecommercezapatillas.dto.DetalleDTO;
import com.example.ecommercezapatillas.dto.ProductoDTO;
import com.example.ecommercezapatillas.entities.*;
import com.example.ecommercezapatillas.entities.enums.Color;
import com.example.ecommercezapatillas.entities.enums.Sexo;
import com.example.ecommercezapatillas.entities.enums.Talle;
import com.example.ecommercezapatillas.repositories.ProductoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService extends BaseService<Producto, Long> {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        super(productoRepository);
    }

    // Convierte Producto a ProductoDTO
    public ProductoDTO convertirADTO(Producto producto) {
        Detalle detalle = producto.getDetalles().stream().findFirst().orElse(null);

        List<String> imagenes = new ArrayList<>();
        if (detalle != null && detalle.getImagenes() != null) {
            imagenes = detalle.getImagenes().stream()
                    .map(Imagen::getDenominacion)
                    .collect(Collectors.toList());
        }

        List<String> categorias = new ArrayList<>();
        if (producto.getCategorias() != null) {
            categorias = producto.getCategorias().stream()
                    .map(Categoria::getDescripcion)
                    .collect(Collectors.toList());
        }

        DetalleDTO detalleDTO = null;
        if (detalle != null) {
            detalleDTO = new DetalleDTO(
                    detalle.getColor() != null ? detalle.getColor().name() : "SIN_COLOR",
                    detalle.getTalle() != null ? detalle.getTalle().name() : "SIN_TALLE",
                    detalle.getMarca(),
                    detalle.getStock(),
                    detalle.getPrecio() != null ? detalle.getPrecio().getPrecioVenta() : 0.0
            );
        }

        return new ProductoDTO(
                producto.getId(),
                producto.getDescripcion(),
                detalle != null && detalle.getPrecio() != null ? detalle.getPrecio().getPrecioCompra() : 0.0,
                detalle != null && detalle.getPrecio() != null ? detalle.getPrecio().getPrecioVenta() : 0.0,
                categorias,
                producto.getSexo(),
                imagenes,
                detalleDTO
        );
    }

    // Obtener todos los productos activos
    public List<ProductoDTO> listarProductosDTO() {
        List<Producto> productos = productoRepository.findByActiveTrue();
        return productos.stream().map(this::convertirADTO).toList();
    }

    // Obtener un producto por ID como DTO
    public ProductoDTO obtenerPorIdDTO(Long id) throws Exception {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            return convertirADTO(producto.get());
        } else {
            throw new Exception("Producto no encontrado con ID: " + id);
        }
    }

    // Filtro de productos con múltiples criterios
    public List<ProductoDTO> filtrarProductosDTO(
            String descripcion, Sexo sexo, String tipoProducto, List<Long> categoriaIds,
            Color color, Talle talle, String marca, Double precioMin, Double precioMax
    ) {
        List<Producto> productos = filtrarProductos(
                descripcion, sexo, tipoProducto, categoriaIds, color, talle, marca, precioMin, precioMax
        );
        return productos.stream().map(this::convertirADTO).toList();
    }

    // Lógica de filtrado con Criteria API
    public List<Producto> filtrarProductos(
            String descripcion,
            Sexo sexo,
            String tipoProducto,
            List<Long> categoriaIds,
            Color color,
            Talle talle,
            String marca,
            Double precioMin,
            Double precioMax
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Producto> cq = cb.createQuery(Producto.class);
        Root<Producto> producto = cq.from(Producto.class);

        Join<Object, Object> detalle = producto.join("detalles", JoinType.LEFT);
        Join<Object, Object> precio = detalle.join("precio", JoinType.LEFT);
        Join<Object, Object> categorias = producto.join("categorias", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (descripcion != null && !descripcion.isEmpty()) {
            predicates.add(cb.like(cb.lower(producto.get("descripcion")), "%" + descripcion.toLowerCase() + "%"));
        }

        if (sexo != null) {
            predicates.add(cb.equal(producto.get("sexo"), sexo));
        }

        if (tipoProducto != null && !tipoProducto.isEmpty()) {
            predicates.add(cb.equal(producto.get("tipoProducto"), tipoProducto));
        }

        if (categoriaIds != null && !categoriaIds.isEmpty()) {
            predicates.add(categorias.get("id").in(categoriaIds));
        }

        if (color != null) {
            predicates.add(cb.equal(detalle.get("color"), color));
        }

        if (talle != null) {
            predicates.add(cb.equal(detalle.get("talle"), talle));
        }

        if (marca != null && !marca.isEmpty()) {
            predicates.add(cb.equal(detalle.get("marca"), marca));
        }

        if (precioMin != null) {
            predicates.add(cb.greaterThanOrEqualTo(precio.get("precioVenta"), precioMin));
        }

        if (precioMax != null) {
            predicates.add(cb.lessThanOrEqualTo(precio.get("precioVenta"), precioMax));
        }

        cq.select(producto).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(cq).getResultList();
    }
}
