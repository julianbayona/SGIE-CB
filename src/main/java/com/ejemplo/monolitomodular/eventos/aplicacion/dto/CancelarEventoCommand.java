package com.ejemplo.monolitomodular.eventos.aplicacion.dto;

import java.util.UUID;

public record CancelarEventoCommand(
        UUID eventoId,
        UUID usuarioId,
        String motivo
) {
}
