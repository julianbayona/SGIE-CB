package com.ejemplo.monolitomodular.eventos.aplicacion.evento;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventoConfirmadoEvent(
        UUID eventoId,
        UUID clienteId,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin
) {
}
