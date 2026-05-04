package com.ejemplo.monolitomodular.pruebasplato.aplicacion.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProgramarPruebaPlatoCommand(
        UUID eventoId,
        UUID usuarioId,
        LocalDateTime fechaRealizacion
) {
}
