package com.ejemplo.monolitomodular.pagos.presentacion.rest.dto;

import com.ejemplo.monolitomodular.pagos.dominio.modelo.EstadoRecordatorioAnticipo;

import java.time.LocalDate;
import java.util.UUID;

public record RecordatorioAnticipoResponse(
        UUID id,
        UUID eventoId,
        UUID usuarioId,
        LocalDate fechaRecordatorio,
        EstadoRecordatorioAnticipo estado,
        UUID notificacionId
) {
}
