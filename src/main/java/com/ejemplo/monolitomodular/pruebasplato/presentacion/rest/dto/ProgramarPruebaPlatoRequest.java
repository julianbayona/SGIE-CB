package com.ejemplo.monolitomodular.pruebasplato.presentacion.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProgramarPruebaPlatoRequest(
        @NotNull UUID usuarioId,
        @NotNull LocalDateTime fechaRealizacion
) {
}
