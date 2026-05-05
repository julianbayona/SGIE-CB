package com.ejemplo.monolitomodular.pagos.presentacion.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record EstadoFinancieroEventoResponse(
        UUID eventoId,
        UUID cotizacionVigenteId,
        BigDecimal valorTotal,
        BigDecimal totalPagado,
        BigDecimal saldoPendiente,
        boolean pagadoTotalmente
) {
}
