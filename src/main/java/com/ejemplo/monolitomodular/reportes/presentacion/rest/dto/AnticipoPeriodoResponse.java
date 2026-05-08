package com.ejemplo.monolitomodular.reportes.presentacion.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AnticipoPeriodoResponse(
        UUID anticipoId,
        UUID eventoId,
        String cliente,
        UUID cotizacionId,
        BigDecimal valor,
        String metodoPago,
        LocalDate fechaPago
) {
}
