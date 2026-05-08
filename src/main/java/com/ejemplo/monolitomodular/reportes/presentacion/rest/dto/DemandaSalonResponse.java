package com.ejemplo.monolitomodular.reportes.presentacion.rest.dto;

import java.util.UUID;

public record DemandaSalonResponse(
        UUID salonId,
        String salon,
        long totalReservas,
        long totalEventos,
        long totalInvitados
) {
}
