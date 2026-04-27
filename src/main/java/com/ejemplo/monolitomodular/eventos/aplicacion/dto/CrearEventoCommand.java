package com.ejemplo.monolitomodular.eventos.aplicacion.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CrearEventoCommand(
        UUID clienteId,
        UUID tipoEventoId,
        UUID tipoComidaId,
        UUID usuarioCreadorId,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin
) {
}
