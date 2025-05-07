package com.example.ecommercezapatillas.services;
import com.example.ecommercezapatillas.dto.DetalleDTO;
import com.example.ecommercezapatillas.dto.ProductoDTO;
import com.example.ecommercezapatillas.entities.Categoria;
import com.example.ecommercezapatillas.entities.Descuentos;
import com.example.ecommercezapatillas.entities.Producto;
import com.example.ecommercezapatillas.entities.ProductoDetalle;
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
    @Transactional
    public List<Producto> findProductosConPromocion() throws Exception  {
        try{
            return productoRepository.findAll().stream()
                    .filter(Producto::isTienePromocion)
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }

    public List<Producto> buscarPorNombre(String keyword)  throws Exception {
        try{
            return productoRepository.findByDenominacionContainingIgnoreCase(keyword);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }

    }
    public ProductoDTO mapearProductoADTO(Producto producto) {
        Double precioFinal = calcularPrecioFinal(producto);

        List<String> imagenesUrls = producto.getImagenes().stream()
                .map(imagen -> imagen.getDenominacion()) // o .getUrl() si tenés eso
                .collect(Collectors.toList());

        List<String> categorias = producto.getCategorias().stream()
                .map(Categoria::getDenominacion)
                .collect(Collectors.toList());

        // Obtenemos un detalle (puede haber muchos, pero usamos el primero como ejemplo)
        ProductoDetalle detalle = producto.getProductos_detalles().stream().findFirst().orElse(null);

        DetalleDTO detalleDTO = null;
        if (detalle != null) {
            detalleDTO = new DetalleDTO(
                    detalle.getPrecioCompra(),
                    detalle.getStockActual(),
                    detalle.getCantidad(),
                    detalle.getStockMaximo(),
                    detalle.getColor().name(),
                    detalle.getTalle().name()
            );
        }

        return new ProductoDTO(
                producto.getId(),
                producto.getDenominacion(),
                producto.getPrecioVenta(),
                precioFinal,
                categorias,
                producto.getSexo(),
                producto.isTienePromocion(),
                imagenesUrls,
                detalleDTO
        );
    }


    public Double calcularPrecioFinal(Producto producto) {
        if (!producto.isTienePromocion()) {
            return producto.getPrecioVenta();
        }

        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        for (Descuentos descuento : producto.getDescuentos()) {
            if ((descuento.getFechaDesde().isBefore(hoy) || descuento.getFechaDesde().isEqual(hoy)) &&
                    (descuento.getFechaHasta().isAfter(hoy) || descuento.getFechaHasta().isEqual(hoy)) &&
                    (descuento.getHoraDesde().isBefore(ahora) || descuento.getHoraDesde().equals(ahora)) &&
                    (descuento.getHoraHasta().isAfter(ahora) || descuento.getHoraHasta().equals(ahora))) {
                return producto.getPrecioVenta() * (1 - descuento.getPrecioPromocional());
            }
        }

        return producto.getPrecioVenta();
    }
    public List<ProductoDTO> obtenerProductosConDTOConDescuento() throws Exception {
        try {
            return productoRepository.findAll().stream()
                    .filter(Producto::isTienePromocion)
                    .map(this::mapearProductoADTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Error al obtener productos con descuento: " + e.getMessage());
        }
    }



}
