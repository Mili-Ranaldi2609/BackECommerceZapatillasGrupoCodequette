package com.example.ecommercezapatillas.auth;

import com.example.ecommercezapatillas.dto.CreateUserRequest;
import com.example.ecommercezapatillas.dto.UpdateUserRequest;
import com.example.ecommercezapatillas.dto.UserDTO;
import com.example.ecommercezapatillas.entities.User;
import com.example.ecommercezapatillas.entities.enums.Rol;
import com.example.ecommercezapatillas.repositories.UserRepository;
import com.example.ecommercezapatillas.services.CloudinaryService;
import com.example.ecommercezapatillas.services.JwtService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class authService {

        private final UserRepository userRepository;
        private final JwtService jwtService;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;
         private final CloudinaryService cloudinaryService; 
        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getUsername(),
                                                request.getPassword()));
                var user = userRepository.findByUsername(request.getUsername())
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

                var jwtToken = jwtService.getToken(user);

                return AuthResponse.builder()
                                .token(jwtToken)
                                .username(user.getUsername())
                                .firstname(user.getFirstname())
                                .lastname(user.getLastname())
                                .role(user.getRole().name())
                                .build();
        }

        public AuthResponse register(RegisterRequest request) {
                var user = User.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))

                                .firstname(request.getFirstname())
                                .lastname(request.getLastname())
                                .direcciones(new HashSet<>()) 
                                .role(request.getRole() != null ? request.getRole() : Rol.USER)
                                .build();
                userRepository.save(user);
                var jwtToken = jwtService.getToken(user);
                return AuthResponse.builder()
                                .token(jwtToken)
                                .build();
        }
        
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDTO(user.getId(), user.getFirstname(), user.getLastname(), user.getUsername(),
                        user.getRole().name(), user.isActive(), user.getProfileImage()))
                .collect(Collectors.toList());
    }

    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con el email: " + request.getUsername());
        }

        User newUser = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .username(request.getUsername()) // Email
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Rol.USER)
                .direcciones(new HashSet<>()) 
                .build();
        User savedUser = userRepository.save(newUser);
        return new UserDTO(savedUser.getId(), savedUser.getFirstname(), savedUser.getLastname(), savedUser.getUsername(),
                savedUser.getRole().name(), savedUser.isActive(), savedUser.getProfileImage()); 
    }
    @Transactional
    public UserDTO updateUser(Long id, UpdateUserRequest request) throws Exception {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con ID: " + id));

        if (request.getFirstname() != null) {
            existingUser.setFirstname(request.getFirstname());
        }
        if (request.getLastname() != null) {
            existingUser.setLastname(request.getLastname());
        }
        if (request.getUsername() != null && !request.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new IllegalArgumentException("El email '" + request.getUsername()
                        + "' ya está en uso por otro usuario.");
            }
            existingUser.setUsername(request.getUsername());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            existingUser.setRole(request.getRole());
        }
        // =========================================================================
        // CORRECCIÓN AQUÍ: Usar isActive() para obtener el valor booleano
        // La propiedad se llama 'active' en el DTO, y para booleanos Lombok genera 'isActive()'
        // O si quieres que acepte nulo para no actualizar si no se envía:
        // if (request.getActive() != null) {
        //     existingUser.setActive(request.getActive());
        // }
        // Si 'active' es un primitivo 'boolean' en el DTO, siempre tendrá un valor (true/false)
        // Entonces, simplemente lo asignas:
        existingUser.setActive(request.isActive()); // <--- CORRECCIÓN

        // =========================================================================

        // Lógica para la imagen de perfil
        if (request.getProfileImage() != null && !request.getProfileImage().trim().isEmpty()) {
            if (!request.getProfileImage().equals(existingUser.getProfileImage())) {
                if (existingUser.getProfileImagePublicId() != null && !existingUser.getProfileImagePublicId().isEmpty()) {
                    try {
                        cloudinaryService.deleteFile(existingUser.getProfileImagePublicId());
                    } catch (Exception e) {
                        System.err.println("Error al eliminar la imagen antigua de Cloudinary (ID: " + existingUser.getProfileImagePublicId() + "): " + e.getMessage());
                    }
                }
                existingUser.setProfileImage(request.getProfileImage());
                existingUser.setProfileImagePublicId(cloudinaryService.extractPublicId(request.getProfileImage()));
            }
        } else {
            if (existingUser.getProfileImagePublicId() != null && !existingUser.getProfileImagePublicId().isEmpty()) {
                try {
                    cloudinaryService.deleteFile(existingUser.getProfileImagePublicId());
                } catch (Exception e) {
                    System.err.println("Error al eliminar la imagen de Cloudinary (quitada) (ID: " + existingUser.getProfileImagePublicId() + "): " + e.getMessage());
                }
            }
            existingUser.setProfileImage(null);
            existingUser.setProfileImagePublicId(null);
        }

        User updatedUser = userRepository.save(existingUser);
        return new UserDTO(updatedUser.getId(), updatedUser.getFirstname(), updatedUser.getLastname(), updatedUser.getUsername(),
                updatedUser.getRole().name(), updatedUser.isActive(), updatedUser.getProfileImage());
    }
    public void deleteUser(Long id) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con ID: " + id));

        userToDelete.setActive(false);
        userRepository.save(userToDelete);
    }
    public void activateUser(Long id) {
        User userToActivate = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con ID: " + id));
        userToActivate.setActive(true);
        userRepository.save(userToActivate);
    }

    public UserDTO getCurrentUserDTO(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return new UserDTO(user.getId(), user.getFirstname(), user.getLastname(), user.getUsername(),
                           user.getRole().name(), user.isActive(), user.getProfileImage());
    }
}
