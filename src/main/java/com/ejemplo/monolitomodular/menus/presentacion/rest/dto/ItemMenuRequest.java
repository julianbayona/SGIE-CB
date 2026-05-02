package com.ejemplo.monolitomodular.menus.presentacion.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ItemMenuRequest(
        @NotNull(message = "El plato es obligatorio")
        UUID platoId,
        @Min(value = 1, message = "La cantidad del item debe ser mayor a cero")
        int cantidad,
        String excepciones
) {
}
