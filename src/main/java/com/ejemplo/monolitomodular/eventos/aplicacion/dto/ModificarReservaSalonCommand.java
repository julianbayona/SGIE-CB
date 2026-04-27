package com.ejemplo.monolitomodular.eventos.aplicacion.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ModificarReservaSalonCommand(
        UUID usuarioId,
        UUID salonId,
        int numInvitados,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin
) {
}
