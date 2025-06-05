package com.example.ecommercezapatillas.services;

import com.example.ecommercezapatillas.dto.CategoriaDTO;
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
import java.util.Map;
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
        List<CategoriaDTO> categoriasDTOList = new ArrayList<>();

        categoriasDTOList = producto.getCategorias().stream()
                .map(categoria -> {
                    return new CategoriaDTO(
                            categoria.getId(),
                            categoria.getDescripcion(),
                            null, // categoriaPadre = null para evitar recursión
                            null, // subcategorias = null para evitar recursión
                            null // productos = null para evitar recursión
                    );
                })
                .collect(Collectors.toList());
        List<DetalleDTO> detallesDTOList = new ArrayList<>();
    if (producto.getDetalles() != null) {
        detallesDTOList = producto.getDetalles().stream()
            .map(d -> {
                List<String> detalleImagenes = new ArrayList<>();
                if (d.getImagenes() != null) {
                    detalleImagenes = d.getImagenes().stream()
                            .map(Imagen::getDenominacion)
                            .collect(Collectors.toList());
                }
                return new DetalleDTO(
                    d.getId(), // ¡Ahora incluimos el ID del detalle!
                    d.getColor() != null ? d.getColor().name() : "SIN_COLOR",
                    d.getTalle() != null ? d.getTalle().name() : "SIN_TALLE",
                    d.getMarca(),
                    d.getStock(),
                    d.getPrecio() != null ? d.getPrecio().getPrecioCompra() : 0.0,
                    d.getPrecio() != null ? d.getPrecio().getPrecioVenta() : 0.0,
                    d.isActive(), // ¡Ahora incluimos el estado 'active' del detalle!
                    detalleImagenes // ¡Ahora incluimos las imágenes del detalle!
                );
            })
            .collect(Collectors.toList());
    }

         return new ProductoDTO(
        producto.getId(),
        producto.getDescripcion(),
        categoriasDTOList,
        producto.getSexo(),
        producto.getTipoProducto(),
        detallesDTOList,    
        producto.isActive() 
    );
    }

    // Obtener todos los productos activos
    public List<ProductoDTO> listarProductosActivosDTO() {
        List<Producto> productos = productoRepository.findByActiveTrue();
        return productos.stream().map(this::convertirADTO).toList();
    }
    //obtener todos los productos para administracion
    public List<ProductoDTO> listarTodosLosProductosDTO() {
        List<Producto> productos = productoRepository.findAll(); // findAll no filtra por 'active'
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
    @Transactional // Asegura que todas las operaciones dentro de este método se ejecuten en una sola transacción
public ProductoDTO crearProducto(ProductoDTO dto) {
    Producto producto = new Producto();
    producto.setDescripcion(dto.getDescripcion());
    producto.setSexo(dto.getSexo());
    producto.setTipoProducto(dto.getTipoProducto());
    producto.setActive(true); // Un producto nuevo siempre está activo por defecto

    // 1. Relacionar categorías
    Set<Categoria> categorias = dto.getCategorias().stream()
            .map(categoriaDTO -> categoriaRepository.findById(categoriaDTO.getId())
                    .orElseThrow(() -> new java.util.NoSuchElementException(
                            "Categoría no encontrada con ID: " + categoriaDTO.getId())))
            .collect(Collectors.toSet());
    producto.setCategorias(categorias);

    // 2. Crear y asociar los detalles al producto
    Set<Detalle> detallesProducto = new HashSet<>(); // Usamos un Set para los detalles del producto

    if (dto.getDetalle() != null && !dto.getDetalle().isEmpty()) {
        for (DetalleDTO detalleDTO : dto.getDetalle()) { // ¡Iteramos sobre CADA DetalleDTO!
            Detalle detalle = new Detalle();
            detalle.setColor(detalleDTO.getColor() != null ? Color.valueOf(detalleDTO.getColor()) : null);
            detalle.setTalle(detalleDTO.getTalle() != null ? Talle.valueOf(detalleDTO.getTalle()) : null);
            detalle.setMarca(detalleDTO.getMarca());
            detalle.setStock(detalleDTO.getStock());
            detalle.setActive(true); // Un nuevo detalle siempre está activo por defecto

            // Crear y asociar el precio
            Precio precio = new Precio();
            precio.setPrecioCompra(detalleDTO.getPrecioCompra());
            precio.setPrecioVenta(detalleDTO.getPrecioVenta());
            detalle.setPrecio(precio); // Asocia el precio al detalle

            // Manejo de Imágenes para CADA Detalle
            List<Imagen> imagenesDetalle = new ArrayList<>();
            if (detalleDTO.getImagenes() != null && !detalleDTO.getImagenes().isEmpty()) {
                for (String urlImagen : detalleDTO.getImagenes()) {
                    Imagen imagen = new Imagen();
                    imagen.setDenominacion(urlImagen);
                    imagen.setActive(true); // Las imágenes también pueden tener un estado activo
                    imagen.setDetalle(detalle); // ¡Importante! Asignar el detalle a la imagen (para @OneToMany)
                    imagenesDetalle.add(imagen);
                }
            }
            detalle.setImagenes(imagenesDetalle); // Asocia las imágenes al detalle

            detalle.setProducto(producto); // Asigna el producto a este detalle
            detallesProducto.add(detalle); // Añade el detalle al set de detalles del producto
        }
    }
    producto.setDetalles(detallesProducto); // Asigna el set de detalles al producto

    // 3. Guardar el producto (con sus categorías, precios, detalles e imágenes en cascada)
    productoRepository.save(producto);

    // 4. Convertir y retornar el DTO del producto creado
    return convertirADTO(producto);
}
@Transactional
public ProductoDTO editarProducto(Long id, ProductoDTO dto) {
    Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new java.util.NoSuchElementException("Producto no encontrado con ID: " + id));

    // 1. Actualizar campos del producto principal
    producto.setDescripcion(dto.getDescripcion());
    producto.setSexo(dto.getSexo());
    producto.setTipoProducto(dto.getTipoProducto());
    producto.setActive(dto.isActive()); 

    // 2. Actualizar Categorías
    // Para las colecciones ManyToMany (como Categorias), la forma de actualizar es un poco diferente
    // Si la relación no tiene orphanRemoval, simplemente reemplazar el Set funciona.
    // Si tuviera orphanRemoval, tendrías que manipular el Set existente.
    Set<Categoria> categorias = dto.getCategorias().stream()
            .map(categoriaDTO -> categoriaRepository.findById(categoriaDTO.getId())
                    .orElseThrow(() -> new java.util.NoSuchElementException(
                            "Categoría no encontrada con ID: " + categoriaDTO.getId())))
            .collect(Collectors.toSet());
    // Es seguro reemplazar un Set en ManyToMany si no tienes orphanRemoval en la relación.
    // Si lo tuvieras, necesitarías copiar los elementos:
    producto.getCategorias().clear(); // Limpia los existentes
    producto.getCategorias().addAll(categorias); // Añade los nuevos
    // producto.setCategorias(categorias); // Esta línea podría haber funcionado si no hay orphanRemoval

    // 3. Manejo de Detalles: Actualizar, Crear y Desactivar Lógicamente

    // Crear un mapa de los detalles existentes del producto por su ID para un acceso rápido
    // (copiamos para no modificar la colección gestionada mientras iteramos y removemos)
    Map<Long, Detalle> existingDetailsMap = producto.getDetalles().stream()
            .filter(d -> d.getId() != null)
            .collect(Collectors.toMap(Detalle::getId, d -> d));

    // Un set para los IDs de los detalles recibidos en el DTO para identificar cuáles se mantienen
    Set<Long> incomingDetalleIds = dto.getDetalle().stream()
            .filter(d -> d.getId() != null)
            .map(DetalleDTO::getId)
            .collect(Collectors.toSet());

    // Iterar sobre los detalles del DTO (los que se enviaron desde el frontend)
    for (DetalleDTO detalleDTO : dto.getDetalle()) {
        Detalle detalle;

        if (detalleDTO.getId() != null && existingDetailsMap.containsKey(detalleDTO.getId())) {
            // Es un detalle existente, recuperarlo del mapa (que tiene las referencias a los objetos originales)
            detalle = existingDetailsMap.get(detalleDTO.getId());
        } else {
            // Es un detalle nuevo (sin ID o ID no encontrado en los existentes)
            detalle = new Detalle();
            detalle.setActive(true); // Nuevos detalles están activos por defecto
            // IMPORTANTE: Si es un detalle nuevo, agrégalo inmediatamente a la colección del producto
            producto.addDetalle(detalle); // Usa el método de conveniencia para manejar la bidireccionalidad
        }

        // Actualizar campos del detalle desde el DTO
        detalle.setColor(detalleDTO.getColor() != null ? Color.valueOf(detalleDTO.getColor()) : null);
        detalle.setTalle(detalleDTO.getTalle() != null ? Talle.valueOf(detalleDTO.getTalle()) : null);
        detalle.setMarca(detalleDTO.getMarca());
        detalle.setStock(detalleDTO.getStock());
        detalle.setActive(detalleDTO.isActive()); // Actualiza el estado 'active' del detalle
        
        // Manejo del precio (actualizar o crear)
        Precio precio;
        if (detalle.getPrecio() != null) {
            precio = detalle.getPrecio(); 
        } else {
            precio = new Precio(); 
            // Si el precio es nuevo, también debe ser asociado al detalle (bidireccionalidad)
            detalle.setPrecio(precio); // Esto también podría necesitar orphanRemoval en el @OneToOne si se remueven precios
        }
        precio.setPrecioCompra(detalleDTO.getPrecioCompra());
        precio.setPrecioVenta(detalleDTO.getPrecioVenta());
        // No es necesario setear detalle.setPrecio(precio) de nuevo si ya existe.

        // Manejo de Imágenes
        // Reemplazar la colección de imágenes del detalle para que orphanRemoval funcione
        List<Imagen> newImagenesForDetalle = new ArrayList<>();
        if (detalleDTO.getImagenes() != null) {
            for (String urlImagen : detalleDTO.getImagenes()) {
                Imagen imagen = new Imagen();
                imagen.setDenominacion(urlImagen);
                imagen.setActive(true); 
                imagen.setDetalle(detalle); 
                newImagenesForDetalle.add(imagen);
            }
        }
        detalle.getImagenes().clear(); // Limpia la colección existente de imágenes para este detalle
        detalle.getImagenes().addAll(newImagenesForDetalle); // Añade las nuevas imágenes


        // En este punto, 'detalle' ya está asociado al 'producto' si fue un detalle nuevo
        // O ya estaba asociado si era existente.
        // No necesitamos añadirlo a un 'updatedDetails' Set para luego hacer producto.setDetalles().
        // Simplemente manipulamos los objetos que JPA ya está gestionando.
    }

    // 4. Desactivar lógicamente los detalles existentes que ya no están en el DTO
    // Usamos un iterador para poder eliminar elementos de la colección mientras la recorremos
    // (aunque en tu caso, estamos solo desactivando, no eliminando del Set del producto).
    // Si necesitas eliminar físicamente, deberías usar `Iterator.remove()`.
    
    // Una copia de la colección para evitar ConcurrentModificationException si la modificamos.
    // Aunque si solo estamos cambiando el estado 'active', no es estrictamente necesario una copia.
    // Es mejor iterar sobre la colección que devuelve producto.getDetalles() directamente
    // y usar un enfoque de eliminación si quieres eliminar, o simplemente actualizar propiedades.
    
    // Para la desactivación lógica:
    producto.getDetalles().forEach(existingDetalle -> {
        // Si el ID de un detalle existente NO está en la lista de IDs recibidos del DTO,
        // significa que fue "eliminado" en el frontend (es decir, no fue enviado de vuelta en la solicitud).
        // Por lo tanto, lo desactivamos lógicamente.
        if (existingDetalle.getId() != null && !incomingDetalleIds.contains(existingDetalle.getId())) {
            existingDetalle.setActive(false);
            // No es necesario añadirlo a 'finalProductDetails' o similar,
            // ya que este 'existingDetalle' es una referencia a un objeto que ya está
            // en la colección gestionada por Hibernate del 'producto'.
        }
    });

    // 5. Guardar el producto. Hibernate detectará los cambios en el producto y sus colecciones gestionadas.
    // Esto es lo que va a persistir los cambios en los detalles existentes, los nuevos detalles,
    // y la desactivación de los detalles que no se enviaron.
    productoRepository.save(producto);

    // 6. Convertir el producto actualizado a DTO y retornarlo
    return convertirADTO(producto);
}
}
