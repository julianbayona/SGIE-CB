package com.ejemplo.monolitomodular.notificaciones.aplicacion.dto;

import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoNotificacion;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificacionView(
        UUID id,
        UUID eventoId,
        UUID tipoNotificacionId,
        LocalDateTime fechaProgramada,
        LocalDateTime fechaEnvio,
        EstadoNotificacion estado,
        int intentos,
        String payloadJson
) {
}
