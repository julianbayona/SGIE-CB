package com.ejemplo.monolitomodular.menus.presentacion.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ConfigurarMenuRequest(
        String notasGenerales,
        @NotEmpty(message = "El menu debe tener al menos una seleccion")
        List<@Valid SeleccionMenuRequest> selecciones
) {
}
