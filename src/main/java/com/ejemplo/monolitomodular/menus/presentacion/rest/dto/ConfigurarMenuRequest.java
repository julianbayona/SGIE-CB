package com.ejemplo.monolitomodular.menus.presentacion.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ConfigurarMenuRequest(
        @NotNull(message = "El usuario es obligatorio")
        UUID usuarioId,
        String notasGenerales,
        @NotEmpty(message = "El menu debe tener al menos una seleccion")
        List<@Valid SeleccionMenuRequest> selecciones
) {
}
