package com.example.ecommercezapatillas.auth;


import com.example.ecommercezapatillas.entities.User;
import com.example.ecommercezapatillas.entities.enums.Rol;
import com.example.ecommercezapatillas.repositories.UserRepository;
import com.example.ecommercezapatillas.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class authService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        var jwtToken = jwtService.getToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
    public AuthResponse register(RegisterRequest request){
        var user= User.builder()
        .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))

                .firstname(request.getFirstname())
        .lastname(request.getLastname())
       .email(request.getEmail())
                .direcciones(new HashSet<>()) // si no las cargás desde el form
                .role(Rol.USER)
        .build();
        userRepository.save(user);
        // 5. Generar el token JWT
        var jwtToken = jwtService.getToken(user);
        return AuthResponse.builder()
        .token(jwtToken)
        .build();
    }
}
