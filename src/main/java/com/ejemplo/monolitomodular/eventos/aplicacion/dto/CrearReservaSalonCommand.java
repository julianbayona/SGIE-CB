package com.ejemplo.monolitomodular.eventos.aplicacion.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CrearReservaSalonCommand(
        UUID usuarioId,
        UUID salonId,
        int numInvitados,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin
) {
}
