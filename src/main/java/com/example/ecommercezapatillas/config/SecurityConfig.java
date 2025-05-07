package com.example.ecommercezapatillas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {}) // activa CORS con configuración externa
                .csrf(csrf -> csrf.disable()) // desactiva CSRF para APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/productos/**",
                                "/menu/**",
                                "/categorias/**",
                                "/imagenes/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
