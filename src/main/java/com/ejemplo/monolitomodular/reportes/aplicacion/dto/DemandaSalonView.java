package com.ejemplo.monolitomodular.reportes.aplicacion.dto;

import java.util.UUID;

public record DemandaSalonView(
        UUID salonId,
        String salon,
        long totalReservas,
        long totalEventos,
        long totalInvitados
) {
}
