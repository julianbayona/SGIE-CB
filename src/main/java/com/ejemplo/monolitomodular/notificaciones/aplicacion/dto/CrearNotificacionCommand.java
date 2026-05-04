package com.ejemplo.monolitomodular.notificaciones.aplicacion.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CrearNotificacionCommand(
        UUID eventoId,
        UUID tipoNotificacionId,
        LocalDateTime fechaProgramada,
        String payloadJson,
        List<Destinatario> destinatarios
) {

    public record Destinatario(
            UUID usuarioId,
            String telefono
    ) {
    }
}
