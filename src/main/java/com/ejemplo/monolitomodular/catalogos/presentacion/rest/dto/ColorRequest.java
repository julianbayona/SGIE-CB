package com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ColorRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        @NotBlank(message = "El codigo hexadecimal es obligatorio")
        String codigoHex
) {
}
