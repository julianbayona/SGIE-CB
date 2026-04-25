package com.ejemplo.monolitomodular.eventos.presentacion.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record CrearEventoRequest(
        @NotNull(message = "El cliente es obligatorio")
        UUID clienteId,
        @NotBlank(message = "El tipo de evento es obligatorio")
        String tipoEvento,
        @NotBlank(message = "El tipo de comida es obligatorio")
        String tipoComida,
        @NotNull(message = "La fecha del evento es obligatoria")
        LocalDate fechaEvento,
        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,
        @Min(value = 2, message = "La duracion minima es de 2 horas")
        int duracionHoras,
        @Min(value = 1, message = "El numero de personas debe ser mayor a cero")
        int numeroPersonas,
        @NotEmpty(message = "Debe asociar al menos un salon")
        List<UUID> salonIds,
        String observaciones,
        UUID usuarioResponsableId
) {
}
