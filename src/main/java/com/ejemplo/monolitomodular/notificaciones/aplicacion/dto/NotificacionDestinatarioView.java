package com.ejemplo.monolitomodular.notificaciones.aplicacion.dto;

import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoDestinatarioNotificacion;

import java.util.UUID;

public record NotificacionDestinatarioView(
        UUID id,
        UUID usuarioId,
        String telefono,
        String correo,
        EstadoDestinatarioNotificacion estado
) {
}
