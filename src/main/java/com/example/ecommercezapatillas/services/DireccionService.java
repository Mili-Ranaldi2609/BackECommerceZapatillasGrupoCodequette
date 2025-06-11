package com.example.ecommercezapatillas.services;

import com.example.ecommercezapatillas.dto.DireccionRequestDTO;
import com.example.ecommercezapatillas.entities.Direccion;
import com.example.ecommercezapatillas.entities.Localidad;
import com.example.ecommercezapatillas.entities.User;
import com.example.ecommercezapatillas.repositories.DireccionRepository;
import com.example.ecommercezapatillas.repositories.LocalidadRepository;
import com.example.ecommercezapatillas.repositories.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DireccionService extends BaseService<Direccion, Long> {
    private final UserRepository userRepository;
    private final LocalidadRepository localidadRepository;

    @Autowired
    public DireccionService(DireccionRepository direccionRepository, UserRepository userRepository,
            LocalidadRepository localidadRepository) {
        super(direccionRepository);
        this.userRepository = userRepository;
        this.localidadRepository = localidadRepository;
    }

    @Transactional
    public List<Direccion> findByUsers_Id(Long userId,boolean activeOnly) throws Exception {
        try {
             if (activeOnly) {
                // Si 'activeOnly' es true, buscamos solo las activas
                return ((DireccionRepository) baseRepository).findByUser_IdAndActive(userId, true);
            } else {
                // Si 'activeOnly' es false o no se pasa, buscamos todas (activas e inactivas)
                return ((DireccionRepository) baseRepository).findByUser_Id(userId);
            }
        } catch (Exception e) {
            throw new Exception(
                    "Error al obtener direcciones para el usuario con ID: " + userId + ". " + e.getMessage());
        }
    }

    @Transactional
    public List<Direccion> listarPorLocalidad(Long idLocalidad) throws Exception {
        try {
            return ((DireccionRepository) baseRepository).findAllByLocalidadId(idLocalidad);
        } catch (Exception e) {
            throw new Exception("Error al listar direcciones por localidad: " + e.getMessage());
        }
    }

    @Transactional
    public Direccion createDireccion(Long userId, DireccionRequestDTO dto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + userId));

        // ¡CAMBIO AQUÍ! Usar dto.getLocalidadId() directamente
        Localidad localidad = localidadRepository.findById(dto.getLocalidadId())
                .orElseThrow(() -> new Exception("Localidad no encontrada con ID: " + dto.getLocalidadId()));

        Direccion newDireccion = Direccion.builder()
                .calle(dto.getCalle())
                .numero(dto.getNumero() != null ? dto.getNumero().intValue() : 0) // Usar intValue() si 'numero' es
                                                                                  // Integer/Number en DTO y entidad es
                                                                                  // 'int'
                .cp(dto.getCp())
                .localidad(localidad)
                .user(user)
                .build();

        newDireccion = baseRepository.save(newDireccion);

        return newDireccion;
    }

    @Transactional
    public Direccion updateDireccion(Long direccionId, Long userId, DireccionRequestDTO dto) throws Exception {
        Direccion existingDireccion = baseRepository.findById(direccionId)
                .orElseThrow(() -> new Exception("Dirección no encontrada con ID: " + direccionId));

        if (existingDireccion.getUser() == null || !existingDireccion.getUser().getId().equals(userId)) {
            throw new Exception(
                    "Acceso denegado: La dirección no pertenece a este usuario o el usuario no está asignado.");
        }

        // ¡CAMBIO AQUÍ! Usar dto.getLocalidadId() directamente
        Localidad localidad = localidadRepository.findById(dto.getLocalidadId())
                .orElseThrow(() -> new Exception("Localidad no encontrada con ID: " + dto.getLocalidadId()));

        existingDireccion.setCalle(dto.getCalle());
        existingDireccion.setNumero(dto.getNumero() != null ? dto.getNumero().intValue() : 0); // Usar intValue() si
                                                                                      // y entidad es 'int'
        existingDireccion.setCp(dto.getCp());
        existingDireccion.setLocalidad(localidad);

        return baseRepository.save(existingDireccion);
    }
    
    @Transactional
    public void deleteDireccion(Long direccionId, Long userId) throws Exception {
        Direccion existingDireccion = baseRepository.findById(direccionId)
                .orElseThrow(() -> new Exception("Dirección no encontrada con ID: " + direccionId));

        if (existingDireccion.getUser() == null || !existingDireccion.getUser().getId().equals(userId)) {
            throw new Exception(
                    "Acceso denegado: La dirección no pertenece a este usuario o el usuario no está asignado.");
        }

        existingDireccion.setActive(false);
        baseRepository.save(existingDireccion);
    }
}
