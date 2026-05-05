package com.ejemplo.monolitomodular.pagos.presentacion.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ProgramarRecordatorioAnticipoRequest(
        @NotNull LocalDate fechaRecordatorio
) {
}
