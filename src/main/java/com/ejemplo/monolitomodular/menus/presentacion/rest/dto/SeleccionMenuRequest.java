package com.ejemplo.monolitomodular.menus.presentacion.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record SeleccionMenuRequest(
        @NotNull(message = "El momento de menu es obligatorio")
        UUID tipoMomentoId,
        @NotEmpty(message = "La seleccion de menu debe tener al menos un item")
        List<@Valid ItemMenuRequest> items
) {
}
