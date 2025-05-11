package com.example.ecommercezapatillas.repositories;

import com.example.ecommercezapatillas.entities.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<User, Integer> {
    Optional<User>findByUsername(String username);
}
