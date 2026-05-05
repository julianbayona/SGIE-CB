package com.ejemplo.monolitomodular.menus.presentacion.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PlatoMomentoRequest(
        @NotNull UUID platoId,
        @NotNull UUID tipoMomentoId
) {
}
