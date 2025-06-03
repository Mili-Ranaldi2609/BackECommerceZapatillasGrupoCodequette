package com.example.ecommercezapatillas.services;

import com.example.ecommercezapatillas.dto.DetalleDTO;
import com.example.ecommercezapatillas.dto.ProductoDTO;
import com.example.ecommercezapatillas.entities.*;
import com.example.ecommercezapatillas.entities.enums.Color;
import com.example.ecommercezapatillas.entities.enums.Sexo;
import com.example.ecommercezapatillas.entities.enums.Talle;
import com.example.ecommercezapatillas.repositories.ProductoRepository;
import com.example.ecommercezapatillas.repositories.CategoriaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductoService extends BaseService<Producto, Long> {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository; // Añade esta línea

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
        List<DetalleDTO> detallesDTOList = new ArrayList<>();
        if (producto.getDetalles() != null) {
            detallesDTOList = producto.getDetalles().stream()
                    .map(d -> new DetalleDTO(
                            null, // O asigna el ID si corresponde, por ejemplo: d.getId()
                            d.getColor() != null ? d.getColor().name() : "SIN_COLOR",
                            d.getTalle() != null ? d.getTalle().name() : "SIN_TALLE",
                            d.getMarca(),
                            d.getStock(),
                            d.getPrecio() != null ? d.getPrecio().getPrecioCompra() : 0.0,
                            d.getPrecio() != null ? d.getPrecio().getPrecioVenta() : 0.0))
                    .collect(Collectors.toList());
        }

        return new ProductoDTO(
                producto.getId(),
                producto.getDescripcion(),
                categorias,
                producto.getSexo(),
                 producto.getTipoProducto(),
                imagenes,
                detallesDTOList
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
            Color color, Talle talle, String marca, Double precioMin, Double precioMax) {
        List<Producto> productos = filtrarProductos(
                descripcion, sexo, tipoProducto, categoriaIds, color, talle, marca, precioMin, precioMax);
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
            Double precioMax) {
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

    // Crear producto (solo 1 detalle por ahora)
    public ProductoDTO crearProducto(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setDescripcion(dto.getDescripcion());
        producto.setSexo(dto.getSexo());
        producto.setActive(true);
        producto.setTipoProducto(dto.getTipoProducto());
        // Relacionar categorías
        List<Categoria> categorias = dto.getCategorias().stream()
                .map(id -> {
                    Categoria c = new Categoria();
                    c.setId(Long.valueOf(id));
                    return c;
                }).collect(Collectors.toList());
        producto.setCategorias(new java.util.HashSet<>(categorias));

        // Crear detalle
        Detalle detalle = new Detalle();
        if (dto.getDetalle() != null && !dto.getDetalle().isEmpty()) {
            DetalleDTO detalleDTO = dto.getDetalle().get(0); // Sigue tomando el primero
            detalle.setColor(detalleDTO.getColor() != null ? Color.valueOf(detalleDTO.getColor()) : null);
            detalle.setTalle(detalleDTO.getTalle() != null ? Talle.valueOf(detalleDTO.getTalle()) : null);
            detalle.setMarca(detalleDTO.getMarca());
            detalle.setStock(detalleDTO.getStock());

            // Crear precio
            Precio precio = new Precio();
            precio.setPrecioCompra(detalleDTO.getPrecioCompra()); // <-- Ahora del DetalleDTO
            precio.setPrecioVenta(detalleDTO.getPrecioVenta()); // <-- Ahora del DetalleDTO

            // Si usaste @OneToOne en Detalle, no necesitas esto:
            // precio.setDetalles(Set.of(detalle));
            detalle.setPrecio(precio);
        }
        detalle.setProducto(producto);

        // Crear imágenes
        List<Imagen> imagenes = dto.getImagenes().stream()
                .map(nombre -> {
                    Imagen img = new Imagen();
                    img.setDenominacion(nombre);
                    img.setDetalles(List.of(detalle));
                    return img;
                }).collect(Collectors.toList());
        detalle.setImagenes(imagenes);

        producto.setDetalles(Set.of(detalle));

        productoRepository.save(producto);
        return convertirADTO(producto);
    }

    @Transactional
    public ProductoDTO editarProducto(Long id, ProductoDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Producto no encontrado"));

        // Actualizar campos del producto
        producto.setDescripcion(dto.getDescripcion());
        producto.setSexo(dto.getSexo());
        producto.setTipoProducto(dto.getTipoProducto());
        Set<Detalle> detallesActuales = new HashSet<>(producto.getDetalles());
        producto.getDetalles().clear();

        for (DetalleDTO detalleDTO : dto.getDetalle()) {
            Detalle detalle;
            if (detalleDTO.getId() != null) {
                detalle = detallesActuales.stream()
                        .filter(d -> d.getId().equals(detalleDTO.getId()))
                        .findFirst()
                        .orElse(new Detalle());
            } else {
                detalle = new Detalle();
            }

            // ... (asigna los campos del DetalleDTO a detalle)
            detalle.setColor(detalleDTO.getColor() != null ? Color.valueOf(detalleDTO.getColor()) : null);
            detalle.setTalle(detalleDTO.getTalle() != null ? Talle.valueOf(detalleDTO.getTalle()) : null);
            detalle.setMarca(detalleDTO.getMarca());
            detalle.setStock(detalleDTO.getStock());

            // Manejo del precio
            Precio precio;
            if (detalle.getPrecio() != null) { // Si ya tiene un precio, actualízalo
                precio = detalle.getPrecio();
            } else {
                precio = new Precio(); // Sino, crea uno nuevo
            }
            precio.setPrecioCompra(detalleDTO.getPrecioCompra());
            precio.setPrecioVenta(detalleDTO.getPrecioVenta());
            detalle.setPrecio(precio);

            detalle.setProducto(producto);
            producto.getDetalles().add(detalle);
        }

        Set<Categoria> nuevasCategorias = dto.getCategorias().stream()
                .map(idStr -> categoriaRepository.findById(Long.valueOf(idStr))
                        .orElseThrow(
                                () -> new java.util.NoSuchElementException("Categoría no encontrada con ID: " + idStr)))
                .collect(Collectors.toSet());
        producto.setCategorias(nuevasCategorias);

        productoRepository.save(producto);
        return convertirADTO(producto);
    }
}
