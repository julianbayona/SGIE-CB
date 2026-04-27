package com.ejemplo.monolitomodular.eventos.presentacion.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CrearEventoRequest(
        @NotNull(message = "El cliente es obligatorio")
        UUID clienteId,
        @NotNull(message = "El tipo de evento es obligatorio")
        UUID tipoEventoId,
        @NotNull(message = "El tipo de comida es obligatorio")
        UUID tipoComidaId,
        @NotNull(message = "El usuario creador es obligatorio")
        UUID usuarioCreadorId,
        @NotNull(message = "La fecha y hora de inicio es obligatoria")
        LocalDateTime fechaHoraInicio,
        @NotNull(message = "La fecha y hora de fin es obligatoria")
        LocalDateTime fechaHoraFin
) {
}
