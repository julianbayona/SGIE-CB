package com.ejemplo.monolitomodular.notificaciones.aplicacion.dto;

import java.util.UUID;

public record EnviarWhatsAppCommand(
        UUID notificacionId,
        String telefono,
        String payloadJson
) {
}
