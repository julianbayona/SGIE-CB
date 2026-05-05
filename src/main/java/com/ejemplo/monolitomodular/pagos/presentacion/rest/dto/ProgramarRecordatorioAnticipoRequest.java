package com.ejemplo.monolitomodular.pagos.presentacion.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ProgramarRecordatorioAnticipoRequest(
        @NotNull UUID usuarioId,
        @NotNull LocalDate fechaRecordatorio
) {
}
