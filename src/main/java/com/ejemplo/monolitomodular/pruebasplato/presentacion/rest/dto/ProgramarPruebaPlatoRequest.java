package com.ejemplo.monolitomodular.pruebasplato.presentacion.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ProgramarPruebaPlatoRequest(
        @NotNull LocalDateTime fechaRealizacion
) {
}
