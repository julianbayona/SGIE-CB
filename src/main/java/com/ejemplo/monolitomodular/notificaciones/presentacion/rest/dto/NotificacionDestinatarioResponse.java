package com.ejemplo.monolitomodular.notificaciones.presentacion.rest.dto;

import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoDestinatarioNotificacion;

import java.util.UUID;

public record NotificacionDestinatarioResponse(
        UUID id,
        UUID usuarioId,
        String telefono,
        String correo,
        EstadoDestinatarioNotificacion estado
) {
}
