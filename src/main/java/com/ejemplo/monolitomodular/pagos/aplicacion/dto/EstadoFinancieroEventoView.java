package com.ejemplo.monolitomodular.pagos.aplicacion.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record EstadoFinancieroEventoView(
        UUID eventoId,
        UUID cotizacionVigenteId,
        BigDecimal valorTotal,
        BigDecimal totalPagado,
        BigDecimal saldoPendiente,
        boolean pagadoTotalmente
) {
}
