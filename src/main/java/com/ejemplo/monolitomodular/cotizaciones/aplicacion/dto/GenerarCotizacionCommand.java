package com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record GenerarCotizacionCommand(
        UUID reservaRaizId,
        UUID usuarioId,
        BigDecimal descuento,
        String observaciones
) {
}
