package com.ejemplo.monolitomodular.pagos.aplicacion.dto;

import com.ejemplo.monolitomodular.pagos.dominio.modelo.EstadoRecordatorioAnticipo;

import java.time.LocalDate;
import java.util.UUID;

public record RecordatorioAnticipoView(
        UUID id,
        UUID eventoId,
        UUID usuarioId,
        LocalDate fechaRecordatorio,
        EstadoRecordatorioAnticipo estado,
        UUID notificacionId
) {
}
