package com.example.ecommercezapatillas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DireccionRequestDTO {

    @NotBlank(message = "La calle es obligatoria.")
    private String calle;

    @NotNull(message = "El número es obligatorio.")
    @Positive(message = "El número debe ser positivo.")
    private Number numero;

    @NotBlank(message = "El código postal es obligatorio.")
    private String cp; // Cambié a String por si hay códigos postales con letras o guiones
    private Long localidadId;
}
