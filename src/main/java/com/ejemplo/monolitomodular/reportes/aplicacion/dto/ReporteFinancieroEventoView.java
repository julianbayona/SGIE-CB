package com.ejemplo.monolitomodular.reportes.aplicacion.dto;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReporteFinancieroEventoView(
        UUID eventoId,
        String cliente,
        LocalDateTime fechaHoraInicio,
        EstadoEvento estado,
        UUID cotizacionId,
        BigDecimal valorTotal,
        BigDecimal totalPagado,
        BigDecimal saldoPendiente,
        boolean pagadoTotalmente
) {
}
