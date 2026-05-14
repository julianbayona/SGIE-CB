package com.ejemplo.monolitomodular.notificaciones.aplicacion.dto;

import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;

import java.util.List;
import java.util.UUID;

public record EnviarEmailCommand(
        UUID notificacionId,
        String correo,
        TipoNotificacion tipo,
        String asunto,
        String cuerpo,
        List<Adjunto> adjuntos
) {
    public EnviarEmailCommand {
        adjuntos = adjuntos == null ? List.of() : List.copyOf(adjuntos);
    }

    public EnviarEmailCommand(UUID notificacionId, String correo, TipoNotificacion tipo, String asunto, String cuerpo) {
        this(notificacionId, correo, tipo, asunto, cuerpo, List.of());
    }

    public record Adjunto(
            String nombreArchivo,
            String contentType,
            byte[] contenido
    ) {
        public Adjunto {
            contenido = contenido == null ? new byte[0] : contenido.clone();
        }

        @Override
        public byte[] contenido() {
            return contenido.clone();
        }
    }
}
