package com.ejemplo.monolitomodular.reportes.aplicacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AnticipoPeriodoView(
        UUID anticipoId,
        UUID eventoId,
        String cliente,
        UUID cotizacionId,
        BigDecimal valor,
        String metodoPago,
        LocalDate fechaPago
) {
}
