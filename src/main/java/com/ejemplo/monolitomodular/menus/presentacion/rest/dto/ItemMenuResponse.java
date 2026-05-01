package com.ejemplo.monolitomodular.menus.presentacion.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemMenuResponse(
        UUID id,
        UUID platoId,
        int cantidad,
        String excepciones,
        BigDecimal precioOverride
) {
}
