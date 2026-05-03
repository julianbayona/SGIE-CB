package com.ejemplo.monolitomodular.pagos.presentacion.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AnticipoResponse(
        UUID id,
        UUID cotizacionId,
        UUID usuarioId,
        BigDecimal valor,
        String metodoPago,
        LocalDate fechaPago,
        String observaciones,
        BigDecimal totalPagado,
        BigDecimal saldoPendiente
) {
}
