package com.example.ecommercezapatillas.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.ecommercezapatillas.services.JwtService;
import com.example.ecommercezapatillas.entities.User;
import com.example.ecommercezapatillas.entities.enums.Rol;
import com.example.ecommercezapatillas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class authService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String token = jwtService.getToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }
    public AuthResponse register(RegisterRequest request){
        User user=User.builder()
        .username(request.getUsername())
        .password(request.getPassword())
        .firstname(request.getFirstname())
        .lastname(request.getLastname())

                .direcciones(new HashSet<>()) // si no las cargás desde el form
                .role(Rol.USER)
        .build();
        userRepository.save(user);
        return AuthResponse.builder()
        .token(jwtService.getToken(user))
        .build();
    }
}
