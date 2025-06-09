package com.example.ecommercezapatillas.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ecommercezapatillas.dto.UpdateUserRequest;
import com.example.ecommercezapatillas.dto.UserDTO;
import com.example.ecommercezapatillas.entities.User;
import com.example.ecommercezapatillas.services.CloudinaryService;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
}
