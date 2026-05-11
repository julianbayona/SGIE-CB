package com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record GenerarCotizacionEventoCommand(
        UUID eventoId,
        UUID usuarioId,
        BigDecimal descuento,
        String observaciones
) {
}
