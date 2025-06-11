package com.example.ecommercezapatillas.services;

import com.example.ecommercezapatillas.dto.CartItemDTO;
import com.example.ecommercezapatillas.dto.OrdenCompraResponseDTO;
import com.example.ecommercezapatillas.dto.OrdenCompraDetalleDTO;
import com.example.ecommercezapatillas.entities.Detalle;
import com.example.ecommercezapatillas.entities.Direccion; 
import com.example.ecommercezapatillas.entities.OrdenCompra;
import com.example.ecommercezapatillas.entities.OrdenCompraDetalle;
import com.example.ecommercezapatillas.entities.User; 
import com.example.ecommercezapatillas.repositories.DetalleRepository;
import com.example.ecommercezapatillas.repositories.OrdenCompraRepository;

import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrdenCompraService extends BaseService<OrdenCompra, Long> {

    private static final Logger logger = LoggerFactory.getLogger(OrdenCompraService.class);

    private final OrdenCompraRepository ordenCompraRepository;
    private final DetalleRepository detalleRepository;

    @Autowired
    public OrdenCompraService(OrdenCompraRepository ordenCompraRepository, DetalleRepository detalleRepository) {
        super(ordenCompraRepository);
        this.ordenCompraRepository = ordenCompraRepository;
        this.detalleRepository = detalleRepository;
    }
    @Transactional
    public OrdenCompraResponseDTO createOrdenCompra(List<CartItemDTO> cartItems, String externalReference, String estadoInicial, User clientUser, Direccion shippingAddress) throws Exception {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("El carrito de compras no puede estar vacío.");
        }
        if (clientUser == null) {
            throw new IllegalArgumentException("El usuario que realiza la compra no puede ser nulo.");
        }
        if (shippingAddress == null) {
            throw new IllegalArgumentException("La dirección de envío no puede ser nula.");
        }

        OrdenCompra ordenCompra = new OrdenCompra();
        ordenCompra.setFechaCompra(LocalDateTime.now());
        ordenCompra.setEstadoPago(estadoInicial);
        ordenCompra.setMpExternalReference(externalReference);
        ordenCompra.setDireccion(shippingAddress); // Establecer la dirección de envío
        ordenCompra.setUsuarioComprador(clientUser); // <--- AÑADE ESTO: Asigna el usuario comprador

        Set<OrdenCompraDetalle> detallesOrden = new HashSet<>();
        float totalOrden = 0.0f;

        for (CartItemDTO itemDTO : cartItems) {
            Detalle detalle = detalleRepository.findById(itemDTO.getId())
                    .orElseThrow(() -> new Exception("Producto (detalle) no encontrado con ID: " + itemDTO.getId()));

            if (detalle.getStock() == null) {
                logger.warn("Producto Detalle ID: {} tiene stock nulo. Asumiendo 0 para verificación.", detalle.getId());
                detalle.setStock(0);
            }

            if (detalle.getStock() < itemDTO.getQuantity()) {
                String productName = (detalle.getProducto() != null && detalle.getProducto().getDescripcion() != null)
                                     ? detalle.getProducto().getDescripcion() : "Producto Desconocido";
                throw new Exception("Stock insuficiente para el producto: " + productName + " (ID Detalle: " + detalle.getId() + "). Disponible: " + detalle.getStock() + ", Solicitado: " + itemDTO.getQuantity());
            }

            OrdenCompraDetalle ordenDetalle = new OrdenCompraDetalle();
            ordenDetalle.setCantidad(itemDTO.getQuantity());
            ordenDetalle.setSubtotal(itemDTO.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())).doubleValue());
            ordenDetalle.setDetalle(detalle);
            ordenDetalle.setOrdenCompra(ordenCompra);

            detallesOrden.add(ordenDetalle);
            totalOrden += ordenDetalle.getSubtotal();
        }

        ordenCompra.setDetalles(detallesOrden);
        ordenCompra.setTotal(totalOrden);

        OrdenCompra savedOrden = ordenCompraRepository.save(ordenCompra);
        String customerName = savedOrden.getUsuarioComprador().getFirstname() + " " + savedOrden.getUsuarioComprador().getLastname();

        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append(savedOrden.getDireccion().getCalle()).append(" ");
        addressBuilder.append(savedOrden.getDireccion().getNumero());
        if (savedOrden.getDireccion().getLocalidad() != null) {
            addressBuilder.append(", ").append(savedOrden.getDireccion().getLocalidad().getNombre());
            if (savedOrden.getDireccion().getLocalidad().getProvincia() != null) {
                 addressBuilder.append(", ").append(savedOrden.getDireccion().getLocalidad().getProvincia().getNombre());
            }
        }
        addressBuilder.append(" CP: ").append(savedOrden.getDireccion().getCp());
        String shippingAddressString = addressBuilder.toString();
        // --- Fin de la lógica para customerName y shippingAddress ---


        List<OrdenCompraDetalleDTO> detallesDTO = savedOrden.getDetalles().stream()
                .map(detalleEntity -> {
                    String productoNombre = (detalleEntity.getDetalle() != null && detalleEntity.getDetalle().getProducto() != null)
                                            ? detalleEntity.getDetalle().getProducto().getDescripcion() : "Producto Desconocido";
                    Long productoId = (detalleEntity.getDetalle() != null && detalleEntity.getDetalle().getProducto() != null)
                                      ? detalleEntity.getDetalle().getProducto().getId() : null;
                    String detalleColor = (detalleEntity.getDetalle() != null && detalleEntity.getDetalle().getColor() != null)
                                          ? detalleEntity.getDetalle().getColor().name() : null;
                    String detalleTalle = (detalleEntity.getDetalle() != null && detalleEntity.getDetalle().getTalle() != null)
                                          ? detalleEntity.getDetalle().getTalle().name() : null;

                    return new OrdenCompraDetalleDTO(
                        detalleEntity.getId(),
                        detalleEntity.getCantidad(),
                        detalleEntity.getSubtotal(),
                        detalleEntity.getDetalle() != null ? detalleEntity.getDetalle().getId() : null,
                        productoId,
                        productoNombre,
                        detalleColor,
                        detalleTalle
                    );
                })
                .collect(Collectors.toList());

        return new OrdenCompraResponseDTO(
                savedOrden.getId(),
                savedOrden.getTotal(),
                savedOrden.getFechaCompra(),
                savedOrden.getEstadoPago(),
                savedOrden.getMpExternalReference(),
                customerName,
                shippingAddressString,
                detallesDTO
        );
    }

    // ... (updateOrdenCompraStatus y deductStockForOrder no necesitan cambios sustanciales aquí) ...
    @Transactional
    public void updateOrdenCompraStatus(String mpExternalReference, String newStatus, Long mpPaymentId) throws Exception {
        OrdenCompra ordenCompra = ordenCompraRepository.findByMpExternalReference(mpExternalReference)
                .orElseThrow(() -> new Exception("Orden de compra no encontrada para la referencia externa: " + mpExternalReference));

        if (!ordenCompra.getEstadoPago().equals("APROBADO") && newStatus.equals("APROBADO")) {
            logger.info("Actualizando estado de orden a APROBADO y descontando stock para externalReference: {}", mpExternalReference);
            ordenCompra.setEstadoPago(newStatus);
            ordenCompra.setMpPaymentId(mpPaymentId);
            ordenCompraRepository.save(ordenCompra);
            deductStockForOrder(ordenCompra.getId());
        } else {
            logger.info("Actualizando estado de orden a {} para externalReference: {}. No se descontó stock.", newStatus, mpExternalReference);
            ordenCompra.setEstadoPago(newStatus);
            ordenCompra.setMpPaymentId(mpPaymentId);
            ordenCompraRepository.save(ordenCompra);
        }
    }

    @Transactional
    public void deductStockForOrder(Long ordenCompraId) throws Exception {
        OrdenCompra ordenCompra = ordenCompraRepository.findById(ordenCompraId)
                .orElseThrow(() -> new Exception("Orden de compra no encontrada con ID: " + ordenCompraId));

        for (OrdenCompraDetalle detalleOrden : ordenCompra.getDetalles()) {
            Detalle productoDetalle = detalleOrden.getDetalle();
            Integer cantidadComprada = detalleOrden.getCantidad();

            if (productoDetalle == null) {
                logger.error("Error: Detalle del producto es nulo para el detalle de orden ID: {}. No se puede descontar stock.", detalleOrden.getId());
                throw new Exception("Error interno: Producto asociado a detalle de orden es nulo.");
            }

            if (productoDetalle.getStock() == null) {
                logger.warn("Producto Detalle ID: {} tiene stock nulo. Asumiendo 0 para descuento.", productoDetalle.getId());
                productoDetalle.setStock(0);
            }

            String productName = (productoDetalle.getProducto() != null && productoDetalle.getProducto().getDescripcion() != null)
                                 ? productoDetalle.getProducto().getDescripcion() : "Producto Desconocido";

            if (productoDetalle.getStock() < cantidadComprada) {
                logger.error("Stock insuficiente para el producto {} (ID Detalle: {}). Stock actual: {}, Cantidad comprada: {}",
                             productName, productoDetalle.getId(), productoDetalle.getStock(), cantidadComprada);
                throw new Exception("Stock insuficiente para el producto " + productName + " (ID Detalle: " + productoDetalle.getId() + ") durante el descuento.");
            }
            productoDetalle.setStock(productoDetalle.getStock() - cantidadComprada);
            detalleRepository.save(productoDetalle);
            logger.info("Stock descontado para el producto {} (ID Detalle: {}). Nuevo stock: {}", productName, productoDetalle.getId(), productoDetalle.getStock());
        }
    }
}