package com.ejemplo.monolitomodular.pagos.aplicacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarAnticipoCommand(
        UUID cotizacionId,
        UUID usuarioId,
        BigDecimal valor,
        String metodoPago,
        LocalDate fechaPago,
        String observaciones
) {
}
