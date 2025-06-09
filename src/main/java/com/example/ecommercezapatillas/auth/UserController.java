package com.example.ecommercezapatillas.auth;
import com.example.ecommercezapatillas.dto.CreateUserRequest; // Importa el DTO de solicitud de creación
import com.example.ecommercezapatillas.dto.UpdateUserRequest; // Importa el DTO de solicitud de actualización
import com.example.ecommercezapatillas.dto.UserDTO; // Importa tu DTO de usuario
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // Para códigos de estado HTTP como 201 Created
import org.springframework.http.ResponseEntity; // Para construir respuestas HTTP
import org.springframework.security.access.prepost.PreAuthorize; // Para la seguridad a nivel de método
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Para manejar usuarios no encontrados
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException; // Para lanzar excepciones con códigos de estado HTTP

import java.util.List;

@RestController
@RequestMapping("/api/admin") // La ruta base para todos los endpoints de administración
@RequiredArgsConstructor // Genera un constructor con 'authService' para inyección de dependencias
public class UserController {

    private final authService authService; 
    @GetMapping("/users") // Ruta completa: /api/admin/users
    @PreAuthorize("hasAuthority('ADMIN')") // Protege el método: solo 'ADMIN' puede acceder
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = authService.getAllUsers(); // Llama al servicio para obtener todos los usuarios
        return ResponseEntity.ok(users); // Devuelve la lista con status 200 OK
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        try {
            UserDTO newUser = authService.createUser(request); // Llama al servicio para crear el usuario
            return new ResponseEntity<>(newUser, HttpStatus.CREATED); // Devuelve 201 Created
        } catch (IllegalArgumentException e) {
            // Si el email ya existe, lanza una excepción con status 409 Conflict
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Endpoint para actualizar un usuario existente por su ID.
     * Solo accesible por usuarios con rol 'ADMIN'.
     *
     * @param id ID del usuario a actualizar.
     * @param request Datos de la actualización.
     * @return ResponseEntity con el UserDTO del usuario actualizado y status 200 OK.
     */
    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        try {
            UserDTO updatedUser = authService.updateUser(id, request); // Llama al servicio para actualizar el usuario
            return ResponseEntity.ok(updatedUser); // Devuelve 200 OK
        } catch (UsernameNotFoundException e) {
            // Si el usuario no se encuentra, lanza una excepción con status 404 Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            // Si el email a actualizar ya está en uso, lanza una excepción con status 409 Conflict
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Endpoint para "eliminar" (desactivar) un usuario por su ID.
     * Realiza una eliminación suave (soft delete) cambiando el estado 'active' a false.
     * Solo accesible por usuarios con rol 'ADMIN'.
     *
     * @param id ID del usuario a eliminar (desactivar).
     * @return ResponseEntity con status 204 No Content si la operación fue exitosa.
     */
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            authService.deleteUser(id); // Llama al servicio para desactivar el usuario
            return ResponseEntity.noContent().build(); // Devuelve 204 No Content (éxito sin contenido de respuesta)
        } catch (UsernameNotFoundException e) {
            // Si el usuario no se encuentra, lanza una excepción con status 404 Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Endpoint para activar un usuario por su ID.
     * Solo accesible por usuarios con rol 'ADMIN'.
     *
     * @param id ID del usuario a activar.
     * @return ResponseEntity con status 200 OK.
     */
    @PostMapping("/users/{id}/activate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        try {
            authService.activateUser(id); // Llama al servicio para activar el usuario
            return ResponseEntity.ok().build(); // Devuelve 200 OK
        } catch (UsernameNotFoundException e) {
            // Si el usuario no se encuentra, lanza una excepción con status 404 Not Found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}