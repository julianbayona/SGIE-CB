package com.ejemplo.monolitomodular.pagos.aplicacion.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ProgramarRecordatorioAnticipoCommand(
        UUID eventoId,
        UUID usuarioId,
        LocalDate fechaRecordatorio
) {
}
