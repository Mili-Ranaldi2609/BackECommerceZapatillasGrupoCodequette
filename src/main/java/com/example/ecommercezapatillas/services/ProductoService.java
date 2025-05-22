package com.example.ecommercezapatillas.services;

import com.example.ecommercezapatillas.dto.DetalleDTO;
import com.example.ecommercezapatillas.dto.ProductoDTO;
import com.example.ecommercezapatillas.entities.Detalle;
import com.example.ecommercezapatillas.entities.Producto;
import com.example.ecommercezapatillas.entities.enums.Color;
import com.example.ecommercezapatillas.entities.enums.Sexo;
import com.example.ecommercezapatillas.entities.enums.Talle;
import com.example.ecommercezapatillas.repositories.DireccionRepository;
import com.example.ecommercezapatillas.repositories.ProductoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductoService extends BaseService<Producto,Long>{

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ProductoRepository productoRepository;
    public ProductoService(ProductoRepository productoRepository){
        super(productoRepository);
    }
    private ProductoDTO convertirADTO(Producto producto) {
        Detalle detalle = producto.getDetalles().stream().findFirst().orElse(null);

        return new ProductoDTO(
                producto.getId(),
                producto.getDescripcion(),
                detalle != null && detalle.getPrecio() != null ? detalle.getPrecio().getPrecioVenta() : 0.0,
                detalle != null && detalle.getPrecio() != null ? detalle.getPrecio().getPrecioVenta() : 0.0,
                producto.getCategorias().stream().map(c -> c.getDescripcion()).toList(),
                producto.getSexo(),
                false,
                detalle != null && detalle.getImagenes() != null
                        ? detalle.getImagenes().stream().map(img -> img.getDenominacion()).toList()
                        : List.of(),
                detalle != null
                        ? new DetalleDTO(
                        detalle.getColor().name(),
                        detalle.getTalle().name(),
                        detalle.getMarca(),
                        detalle.getStock(),
                        detalle.getPrecio() != null ? detalle.getPrecio().getPrecioVenta() : 0.0
                )
                        : null
        );
    }
    public List<ProductoDTO> filtrarProductosDTO(
            String descripcion, Sexo sexo, String tipoProducto, List<Long> categoriaIds,
            Color color, Talle talle, String marca, Double precioMin, Double precioMax
    ) {
        List<Producto> productos = filtrarProductos(
                descripcion, sexo, tipoProducto, categoriaIds, color, talle, marca, precioMin, precioMax
        );
        return productos.stream().map(this::convertirADTO).toList();
    }


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
    public List<ProductoDTO> listarProductosDTO() {
        List<Producto> productos = productoRepository.findByActiveTrue(); // si usás borrado lógico
        return productos.stream().map(this::convertirADTO).toList();
    }


}
