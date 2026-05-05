package com.ejemplo.monolitomodular.eventos.presentacion.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ModificarReservaSalonRequest(
        @NotNull(message = "El salon es obligatorio")
        UUID salonId,
        @Min(value = 1, message = "El numero de invitados debe ser mayor a cero")
        int numInvitados,
        @NotNull(message = "La fecha y hora de inicio es obligatoria")
        LocalDateTime fechaHoraInicio,
        @NotNull(message = "La fecha y hora de fin es obligatoria")
        LocalDateTime fechaHoraFin
) {
}
