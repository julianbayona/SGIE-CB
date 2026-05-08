package com.ejemplo.monolitomodular.notificaciones.presentacion.rest.dto;

import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoNotificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record NotificacionResponse(
        UUID id,
        UUID eventoId,
        TipoNotificacion tipo,
        LocalDateTime fechaProgramada,
        LocalDateTime fechaEnvio,
        EstadoNotificacion estado,
        int intentos,
        String payloadJson,
        List<NotificacionDestinatarioResponse> destinatarios
) {
}
