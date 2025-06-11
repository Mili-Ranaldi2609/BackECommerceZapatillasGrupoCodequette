package com.example.ecommercezapatillas.controllers;

import com.example.ecommercezapatillas.dto.CartItemDTO;
import com.example.ecommercezapatillas.dto.OrdenCompraResponseDTO;
import com.example.ecommercezapatillas.entities.Direccion;
import com.example.ecommercezapatillas.entities.User;
import com.example.ecommercezapatillas.repositories.DireccionRepository;
import com.example.ecommercezapatillas.services.OrdenCompraService;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.net.MPResponse;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/payments")
public class MercadoPagoController {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoController.class);

    @Value("${MP_ACCESS_TOKEN}")
    private String accesToken;

    @Value("${MERCADOPAGO_NOTIFICATION_URL}")
    private String notificationUrl;

    @Value("${MERCADOPAGO_SUCCESS_URL}")
    private String successUrl;

    @Value("${MERCADOPAGO_PENDING_URL}")
    private String pendingUrl;

    @Value("${MERCADOPAGO_FAILURE_URL}")
    private String failureUrl;

    private final OrdenCompraService ordenCompraService;
    private final DireccionRepository direccionRepository;

    public MercadoPagoController(OrdenCompraService ordenCompraService, DireccionRepository direccionRepository) {
        this.ordenCompraService = ordenCompraService;
        this.direccionRepository = direccionRepository;
    }

    @PostMapping("/create-preference/{direccionId}")
    public ResponseEntity<String> createCheckout(@RequestBody List<CartItemDTO> cartItems,
            @PathVariable Long direccionId) {
        try {
            com.mercadopago.MercadoPagoConfig.setAccessToken(accesToken);
            logger.info("Token Mercado Pago configurado.");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"message\":\"Usuario no autenticado o no válido\"}");
            }
            User currentUser = (User) authentication.getPrincipal();

            Direccion shippingAddress = direccionRepository.findById(direccionId)
                    .orElseThrow(() -> new Exception("Dirección de envío no encontrada con ID: " + direccionId));
            if (shippingAddress.getUser() == null || !shippingAddress.getUser().getId().equals(currentUser.getId())) {
                throw new Exception("La dirección de envío seleccionada no pertenece al usuario actual.");
            }
            String externalReference = "ORDER-" + UUID.randomUUID().toString();
            logger.info("Generando referencia externa para la orden: {}", externalReference);
            OrdenCompraResponseDTO ordenResponse = ordenCompraService.createOrdenCompra(
                    cartItems,
                    externalReference,
                    "PENDIENTE_PAGO_MP",
                    currentUser,
                    shippingAddress);
            logger.info("Orden de compra creada en la DB con ID: {} y externalReference: {}", ordenResponse.getId(),
                    ordenResponse.getExternalReference());

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(successUrl)
                    .pending(pendingUrl)
                    .failure(failureUrl)
                    .build();

            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .email(currentUser.getUsername())
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            for (CartItemDTO cartItem : cartItems) {
                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                        .id(String.valueOf(cartItem.getId()))
                        .title(cartItem.getName())
                        .pictureUrl(cartItem.getImageUrl())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getPrice().setScale(2, RoundingMode.HALF_UP))
                        .currencyId("ARS")
                        .build();
                items.add(itemRequest);
            }

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .backUrls(backUrls)
                    .notificationUrl(notificationUrl)
                    .externalReference(externalReference)
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            String prefId = preference.getId();
            logger.info("Mercado Pago Preference ID generado: {}", prefId);
            if (prefId == null || prefId.isEmpty()) {
                logger.error("El Preference ID de Mercado Pago es nulo o vacío después de la creación.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"message\":\"Error: El Preference ID de Mercado Pago es nulo o vacío.\"}");
            }
            return ResponseEntity.status(HttpStatus.OK).body("{\"preferenceId\":\"" + prefId + "\"}");

        } catch (MPApiException mpEx) {
            MPResponse response = mpEx.getApiResponse();
            logger.error("Error de API de Mercado Pago. Status code: {}, Response body: {}", response.getStatusCode(),
                    response.getContent());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la preferencia: " + response.getContent());
        } catch (Exception e) {
            logger.error("Error inesperado al crear la preferencia de pago: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleMercadoPagoWebhook(
            @RequestParam(name = "topic", required = false) String topic,
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "data.id", required = false) String dataId) {
        String resourceId = id != null ? id : dataId;

        logger.info("Webhook de Mercado Pago recibido. Topic: {}, Resource ID: {}", topic, resourceId);

        if (resourceId == null || topic == null) {
            logger.warn("Webhook recibido con parámetros incompletos. Topic: {}, ID: {}", topic, resourceId);
            return ResponseEntity.badRequest().build();
        }

        if ("payment".equals(topic)) {
            try {
                com.mercadopago.MercadoPagoConfig.setAccessToken(accesToken);

                com.mercadopago.client.payment.PaymentClient paymentClient = new com.mercadopago.client.payment.PaymentClient();
                Payment payment = paymentClient.get(Long.valueOf(resourceId));
                logger.info("Detalles del pago desde MP - ID: {}, Estado: {}, Referencia Externa: {}",
                        payment.getId(), payment.getStatus(), payment.getExternalReference());
                String estadoInterno = mapMercadoPagoStatusToInternal(payment.getStatus());
                ordenCompraService.updateOrdenCompraStatus(payment.getExternalReference(), estadoInterno,
                        payment.getId());
                logger.info("Estado de la orden de compra actualizado en DB. Referencia Externa: {}, Nuevo Estado: {}",
                        payment.getExternalReference(), estadoInterno);

            } catch (MPApiException mpEx) {
                logger.error("Error de API al obtener detalles del pago MP. Status: {}, Content: {}",
                        mpEx.getApiResponse().getStatusCode(), mpEx.getApiResponse().getContent());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            } catch (Exception e) {
                logger.error("Error inesperado al procesar webhook de pago: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            logger.warn("Topic de webhook no manejado: {}", topic);
        }
        return ResponseEntity.ok().build();
    }

    private String mapMercadoPagoStatusToInternal(String mpStatus) {
        switch (mpStatus) {
            case "approved":
                return "APROBADO";
            case "pending":
            case "in_process":
                return "PENDIENTE";
            case "rejected":
                return "RECHAZADO";
            case "cancelled":
                return "CANCELADO";
            case "refunded":
                return "REEMBOLSADO";
            case "charged_back":
                return "CONTRACARGO";
            default:
                return "DESCONOCIDO";
        }
    }
}