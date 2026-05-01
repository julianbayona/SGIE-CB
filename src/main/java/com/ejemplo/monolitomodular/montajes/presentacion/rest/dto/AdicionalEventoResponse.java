package com.ejemplo.monolitomodular.montajes.presentacion.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AdicionalEventoResponse(
        UUID id,
        UUID tipoAdicionalId,
        int cantidad,
        BigDecimal precioOverride
) {
}
