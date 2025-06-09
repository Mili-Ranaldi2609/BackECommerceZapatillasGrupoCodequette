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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private CategoriaRepository categoriaRepository;
    @Autowired
    private CloudinaryService cloudinaryService;

    public ProductoService(ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            CloudinaryService cloudinaryService) {
        super(productoRepository); 
        this.productoRepository = productoRepository; 
        this.categoriaRepository = categoriaRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        return cloudinaryService.uploadFile(file);
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
                                    .map(Imagen::getUrl)
                                    .collect(Collectors.toList());
                        }
                        return new DetalleDTO(
                                d.getId(),
                                d.getColor() != null ? d.getColor().name() : "SIN_COLOR",
                                d.getTalle() != null ? d.getTalle().name() : "SIN_TALLE",
                                d.getMarca(),
                                d.getStock(),
                                d.getPrecio() != null ? d.getPrecio().getPrecioCompra() : 0.0,
                                d.getPrecio() != null ? d.getPrecio().getPrecioVenta() : 0.0,
                                d.isActive(),
                                detalleImagenes);
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
                producto.isActive());
    }

    // Obtener todos los productos activos
    public List<ProductoDTO> listarProductosActivosDTO() {
        List<Producto> productos = productoRepository.findByActiveTrue();
        return productos.stream().map(this::convertirADTO).toList();
    }

    // obtener todos los productos para administracion
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

    @Transactional
    public ProductoDTO crearProducto(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setDescripcion(dto.getDescripcion());
        producto.setSexo(dto.getSexo());
        producto.setTipoProducto(dto.getTipoProducto());
        producto.setActive(true);

        Set<Categoria> categorias = dto.getCategorias().stream()
                .map(categoriaDTO -> categoriaRepository.findById(categoriaDTO.getId())
                        .orElseThrow(() -> new java.util.NoSuchElementException(
                                "Categoría no encontrada con ID: " + categoriaDTO.getId())))
                .collect(Collectors.toSet());
        producto.setCategorias(categorias);

        Set<Detalle> detallesProducto = new HashSet<>();
        if (dto.getDetalle() != null && !dto.getDetalle().isEmpty()) {
            for (DetalleDTO detalleDTO : dto.getDetalle()) {
                Detalle detalle = new Detalle();
                detalle.setColor(detalleDTO.getColor() != null ? Color.valueOf(detalleDTO.getColor()) : null);
                detalle.setTalle(detalleDTO.getTalle() != null ? Talle.valueOf(detalleDTO.getTalle()) : null);
                detalle.setMarca(detalleDTO.getMarca());
                detalle.setStock(detalleDTO.getStock());
                detalle.setActive(true);

                Precio precio = new Precio();
                precio.setPrecioCompra(detalleDTO.getPrecioCompra());
                precio.setPrecioVenta(detalleDTO.getPrecioVenta());
                detalle.setPrecio(precio);

                List<Imagen> imagenesDetalle = new ArrayList<>();
                if (detalleDTO.getImagenes() != null && !detalleDTO.getImagenes().isEmpty()) {
                    for (String urlImagen : detalleDTO.getImagenes()) { // urlImagen ya está definida aquí
                        Imagen imagen = new Imagen();
                        imagen.setUrl(urlImagen); // Usar setUrl()
                        imagen.setPublicId(cloudinaryService.extractPublicId(urlImagen)); // Guardar publicId
                        imagen.setActive(true);
                        imagen.setDetalle(detalle);
                        imagenesDetalle.add(imagen);
                    }
                }
                detalle.setImagenes(imagenesDetalle);
                // ...
                detalle.setProducto(producto);
                detallesProducto.add(detalle);
            }
        }
        producto.setDetalles(detallesProducto);

        productoRepository.save(producto);
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
        Set<Categoria> categorias = dto.getCategorias().stream()
                .map(categoriaDTO -> categoriaRepository.findById(categoriaDTO.getId())
                        .orElseThrow(() -> new java.util.NoSuchElementException(
                                "Categoría no encontrada con ID: " + categoriaDTO.getId())))
                .collect(Collectors.toSet());
        producto.getCategorias().clear();
        producto.getCategorias().addAll(categorias);

        Map<Long, Detalle> existingDetailsMap = producto.getDetalles().stream()
                .filter(d -> d.getId() != null)
                .collect(Collectors.toMap(Detalle::getId, d -> d));
        Set<Long> incomingDetalleIds = dto.getDetalle().stream()
                .filter(d -> d.getId() != null)
                .map(DetalleDTO::getId)
                .collect(Collectors.toSet());

        for (DetalleDTO detalleDTO : dto.getDetalle()) {
            Detalle detalle;

            if (detalleDTO.getId() != null && existingDetailsMap.containsKey(detalleDTO.getId())) {
                detalle = existingDetailsMap.get(detalleDTO.getId());
            } else {
                detalle = new Detalle();
                detalle.setActive(true);
                producto.addDetalle(detalle);
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
                detalle.setPrecio(precio);
            }
            precio.setPrecioCompra(detalleDTO.getPrecioCompra());
            precio.setPrecioVenta(detalleDTO.getPrecioVenta());

            // --- Manejo de Imágenes dentro de cada Detalle ---
            // Crear un mapa de las URLs de las imágenes existentes para el detalle actual
            Map<String, Imagen> existingImagesMap = detalle.getImagenes().stream()
                    .filter(img -> img.getUrl() != null)
                    .collect(Collectors.toMap(Imagen::getUrl, img -> img));

            Set<String> incomingImageUrls = new HashSet<>();
            if (detalleDTO.getImagenes() != null) {
                incomingImageUrls = new HashSet<>(detalleDTO.getImagenes());
            }

            // Eliminar imágenes que ya no están en el DTO para este detalle
            List<Imagen> imagesToRemove = new ArrayList<>();
            for (Imagen existingImage : detalle.getImagenes()) {
                if (!incomingImageUrls.contains(existingImage.getUrl())) {
                    imagesToRemove.add(existingImage);
                    // Eliminar de Cloudinary
                    if (existingImage.getPublicId() != null) {
                        try {
                            cloudinaryService.deleteFile(existingImage.getPublicId());
                        } catch (IOException e) {
                            System.err.println("Error al eliminar imagen de Cloudinary: " + existingImage.getPublicId()
                                    + " - " + e.getMessage());

                        }
                    }
                }
            }
            // Remover las imágenes marcadas para eliminación de la colección del detalle
            detalle.getImagenes().removeAll(imagesToRemove);

            // Añadir nuevas imágenes
            if (detalleDTO.getImagenes() != null) {
                for (String newImageUrl : detalleDTO.getImagenes()) {
                    if (!existingImagesMap.containsKey(newImageUrl)) {
                        Imagen newImagen = new Imagen();
                        newImagen.setUrl(newImageUrl);
                        newImagen.setPublicId(cloudinaryService.extractPublicId(newImageUrl));
                        newImagen.setActive(true);
                        newImagen.setDetalle(detalle);
                        detalle.getImagenes().add(newImagen);
                    }
                }
            }
        }

        // 4. Desactivar lógicamente los detalles existentes que ya no están en el DTO
        producto.getDetalles().forEach(existingDetalle -> {
            if (existingDetalle.getId() != null && !incomingDetalleIds.contains(existingDetalle.getId())) {
                existingDetalle.setActive(false);
                existingDetalle.getImagenes().forEach(img -> {
                    img.setActive(false);
                    // para eliminar desde cloudinary
                    // if (img.getPublicId() != null) {
                    // try {
                    // cloudinaryService.deleteFile(img.getPublicId());
                    // } catch (IOException e) {
                    // System.err.println("Error al eliminar imagen de Cloudinary al desactivar
                    // detalle: " + img.getPublicId() + " - " + e.getMessage());
                    // }
                    // }
                });
            }
        });
        productoRepository.save(producto);

        return convertirADTO(producto);
    }

}
