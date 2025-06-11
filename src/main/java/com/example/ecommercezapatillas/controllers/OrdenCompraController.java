package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.dto.CartItemDTO;
import com.example.ecommercezapatillas.dto.OrdenCompraResponseDTO;
import com.example.ecommercezapatillas.entities.Direccion;
import com.example.ecommercezapatillas.entities.OrdenCompra;
import com.example.ecommercezapatillas.entities.User;
import com.example.ecommercezapatillas.services.OrdenCompraService;
import com.example.ecommercezapatillas.repositories.DireccionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/ordenes")
public class OrdenCompraController extends BaseController<OrdenCompra, Long> {

    private final OrdenCompraService ordenCompraService;
    private final DireccionRepository direccionRepository;

    @Autowired
    public OrdenCompraController(OrdenCompraService ordenCompraService, DireccionRepository direccionRepository) {
        super(ordenCompraService);
        this.ordenCompraService = ordenCompraService;
        this.direccionRepository = direccionRepository;
    }

    @PostMapping("/crear-efectivo/{direccionId}")
    public ResponseEntity<OrdenCompraResponseDTO> createOrdenEfectivo(
            @RequestBody List<CartItemDTO> cartItems,
            @PathVariable Long direccionId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            User currentUser = (User) authentication.getPrincipal();
            Direccion shippingAddress = direccionRepository.findById(direccionId)
                    .orElseThrow(() -> new Exception("Dirección de envío no encontrada con ID: " + direccionId));
            if (shippingAddress.getUser() == null || !shippingAddress.getUser().getId().equals(currentUser.getId())) {
                throw new Exception("La dirección de envío seleccionada no pertenece al usuario actual.");
            }

            String externalReference = "EFE-" + UUID.randomUUID().toString();

            OrdenCompraResponseDTO ordenResponse = ordenCompraService.createOrdenCompra(
                    cartItems,
                    externalReference,
                    "PENDIENTE_PAGO_EFECTIVO",
                    currentUser,
                    shippingAddress);

            ordenCompraService.deductStockForOrder(ordenResponse.getId());
            ordenResponse.setEstado("PAGADO_EFECTIVO");

            return ResponseEntity.status(HttpStatus.CREATED).body(ordenResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}