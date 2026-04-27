package com.ejemplo.monolitomodular.eventos.aplicacion.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReservaSalonView(
        UUID id,
        UUID reservaRaizId,
        UUID salonId,
        int numInvitados,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        String estado,
        int version,
        boolean vigente
) {
}
