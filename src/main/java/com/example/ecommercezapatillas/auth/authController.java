package com.example.ecommercezapatillas.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.ecommercezapatillas.dto.DireccionRequestDTO;
import com.example.ecommercezapatillas.dto.DireccionResponseDTO;
import com.example.ecommercezapatillas.dto.LocalidadDTO;
import com.example.ecommercezapatillas.dto.LocalidadResponseDTO;
import com.example.ecommercezapatillas.dto.ProvinciaDTO;
import com.example.ecommercezapatillas.dto.ProvinciaResponseDTO;
import com.example.ecommercezapatillas.dto.UpdateUserRequest;
import com.example.ecommercezapatillas.dto.UserDTO;
import com.example.ecommercezapatillas.entities.Direccion;
import com.example.ecommercezapatillas.entities.Localidad;
import com.example.ecommercezapatillas.entities.Provincia;
import com.example.ecommercezapatillas.entities.User;
import com.example.ecommercezapatillas.services.CloudinaryService;
import com.example.ecommercezapatillas.services.DireccionService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class authController {
    private final authService authService;
    private final CloudinaryService cloudinaryService; 
    private final DireccionService direccionService;
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal User user) {

        UserDTO dto = authService.getCurrentUserDTO(user.getUsername());
        return ResponseEntity.ok(dto);
    }
     @PutMapping("/me") 
    public ResponseEntity<UserDTO> updateCurrentUser(@AuthenticationPrincipal User user, @RequestBody UpdateUserRequest request) {
        try {
            UserDTO updatedUserDTO = authService.updateUser(user.getId(), request);
            return ResponseEntity.ok(updatedUserDTO);
        } catch (Exception e) {
            System.err.println("Error al actualizar perfil de usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(null);
        }
    }
     @PostMapping("/upload-profile-image")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "El archivo no puede estar vacío."));
        }
        try {
            String imageUrl = cloudinaryService.uploadFile(file); 
            return ResponseEntity.ok(Collections.singletonMap("url", imageUrl)); 
        } catch (IOException e) {
            System.err.println("Error de I/O al subir la imagen de perfil: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("error", "Error de I/O al subir la imagen de perfil: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error inesperado al subir la imagen de perfil: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("error", "Error inesperado al subir la imagen de perfil: " + e.getMessage()));
        }
    }
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
      @GetMapping("/direcciones")
    public ResponseEntity<List<DireccionResponseDTO>> getUserAddresses(@RequestParam(required = false, defaultValue = "false") boolean activeOnly) { 
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            User currentUser = (User) authentication.getPrincipal();
            Long userId = currentUser.getId(); 
            List<Direccion> direcciones = direccionService.findByUsers_Id(userId,activeOnly); 
            
            List<DireccionResponseDTO> direccionDTOs = direcciones.stream()
                .map(direccion -> {
                    Localidad localidadEntity = direccion.getLocalidad();
                    Provincia provinciaEntity = localidadEntity != null ? localidadEntity.getProvincia() : null;

                    ProvinciaResponseDTO provinciaDto = null;
                    if (provinciaEntity != null) {
                        provinciaDto = ProvinciaResponseDTO.builder()
                            .id(provinciaEntity.getId())
                            .nombre(provinciaEntity.getNombre()) 
                            .build();
                    }

                    LocalidadResponseDTO localidadDto = null;
                    if (localidadEntity != null) {
                        localidadDto = LocalidadResponseDTO.builder()
                            .id(localidadEntity.getId())
                            .nombre(localidadEntity.getNombre())
                            .provincia(provinciaDto)
                            .build();
                    }

                    return DireccionResponseDTO.builder()
                        .id(direccion.getId())
                        .calle(direccion.getCalle())
                        .numero(direccion.getNumero().intValue()) 
                        .cp(direccion.getCp())
                        .localidad(localidadDto)
                        .build();
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(direccionDTOs);

        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); 
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor al obtener direcciones.");
        }
    }
    @PostMapping("/direcciones") 
    public ResponseEntity<DireccionResponseDTO> createDireccion(@RequestBody DireccionRequestDTO direccionDto) { 
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            User currentUser = (User) authentication.getPrincipal();
            Long userId = currentUser.getId(); 

            Direccion nuevaDireccion = direccionService.createDireccion(userId, direccionDto);
            
            // ¡Mapear la entidad creada a DireccionResponseDTO!
            Localidad localidadEntity = nuevaDireccion.getLocalidad();
            Provincia provinciaEntity = localidadEntity != null ? localidadEntity.getProvincia() : null;

            ProvinciaResponseDTO provinciaDto = null;
            if (provinciaEntity != null) {
                provinciaDto = ProvinciaResponseDTO.builder()
                    .id(provinciaEntity.getId())
                    .nombre(provinciaEntity.getNombre()) 
                    .build();
            }

            LocalidadResponseDTO localidadDto = null;
            if (localidadEntity != null) {
                localidadDto = LocalidadResponseDTO.builder()
                    .id(localidadEntity.getId())
                    .nombre(localidadEntity.getNombre())
                    .provincia(provinciaDto)
                    .build();
            }

            DireccionResponseDTO responseDto = DireccionResponseDTO.builder()
                .id(nuevaDireccion.getId())
                .calle(nuevaDireccion.getCalle())
                .numero(nuevaDireccion.getNumero() != null ? nuevaDireccion.getNumero().intValue() : 0)
                .cp(nuevaDireccion.getCp())
                .localidad(localidadDto)
                .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (Exception e) {
            e.printStackTrace(); // Es crucial ver el stack trace completo
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PutMapping("/direcciones/{direccionId}")
    // ¡CAMBIO AQUÍ! Ahora devuelve DireccionResponseDTO
    public ResponseEntity<DireccionResponseDTO> updateDireccion(@PathVariable Long direccionId, @RequestBody DireccionRequestDTO direccionDto) { 
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            User currentUser = (User) authentication.getPrincipal();
            Long userId = currentUser.getId();
            Direccion direccionActualizada = direccionService.updateDireccion(direccionId, userId, direccionDto);
            
            // ¡Mapear la entidad actualizada a DireccionResponseDTO!
            Localidad localidadEntity = direccionActualizada.getLocalidad();
            Provincia provinciaEntity = localidadEntity != null ? localidadEntity.getProvincia() : null;

            ProvinciaResponseDTO provinciaDto = null;
            if (provinciaEntity != null) {
                provinciaDto = ProvinciaResponseDTO.builder()
                    .id(provinciaEntity.getId())
                    .nombre(provinciaEntity.getNombre())
                    .build();
            }

            LocalidadResponseDTO localidadDto = null;
            if (localidadEntity != null) {
                localidadDto = LocalidadResponseDTO.builder()
                    .id(localidadEntity.getId())
                    .nombre(localidadEntity.getNombre())
                    .provincia(provinciaDto)
                    .build();
            }

            DireccionResponseDTO responseDto = DireccionResponseDTO.builder()
                .id(direccionActualizada.getId())
                .calle(direccionActualizada.getCalle())
                .numero(direccionActualizada.getNumero() != null ? direccionActualizada.getNumero().intValue() : 0)
                .cp(direccionActualizada.getCp())
                .localidad(localidadDto)
                .build();

            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            e.printStackTrace(); // Es crucial ver el stack trace completo
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/direcciones/{direccionId}") 
    public ResponseEntity<Void> deleteDireccion(@PathVariable Long direccionId) { 
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            User currentUser = (User) authentication.getPrincipal();
            Long userId = currentUser.getId();
            direccionService.deleteDireccion(direccionId, userId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

    



