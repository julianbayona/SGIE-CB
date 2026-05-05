package com.ejemplo.monolitomodular.notificaciones.aplicacion.dto;

import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CrearNotificacionCommand(
        UUID eventoId,
        TipoNotificacion tipo,
        LocalDateTime fechaProgramada,
        String payloadJson,
        List<Destinatario> destinatarios
) {

    public record Destinatario(
            UUID usuarioId,
            String telefono,
            String correo
    ) {
        public Destinatario(UUID usuarioId, String telefono) {
            this(usuarioId, telefono, null);
        }
    }
}
