package com.ejemplo.monolitomodular.notificaciones.aplicacion.dto;

import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;

import java.util.UUID;

public record EnviarEmailCommand(
        UUID notificacionId,
        String correo,
        TipoNotificacion tipo,
        String asunto,
        String cuerpo
) {
}
