package com.ejemplo.monolitomodular.auth.presentacion.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String nombre,
        @NotBlank String contrasena
) {
}
