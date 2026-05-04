package com.ejemplo.monolitomodular.eventos.presentacion.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConfirmarEventoRequest(
        @NotNull UUID usuarioId
) {
}
