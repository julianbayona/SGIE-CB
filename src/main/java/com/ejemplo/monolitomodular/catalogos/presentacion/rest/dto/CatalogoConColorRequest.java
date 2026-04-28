package com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CatalogoConColorRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        @NotNull(message = "El color es obligatorio")
        UUID colorId
) {
}
