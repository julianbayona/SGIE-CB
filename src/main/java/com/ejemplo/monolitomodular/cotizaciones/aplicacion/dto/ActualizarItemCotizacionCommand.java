package com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ActualizarItemCotizacionCommand(
        UUID cotizacionId,
        UUID itemId,
        BigDecimal precioOverride
) {
}
