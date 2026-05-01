package com.ejemplo.monolitomodular.menus.aplicacion.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemMenuCommand(
        UUID platoId,
        int cantidad,
        String excepciones,
        BigDecimal precioOverride
) {
}
