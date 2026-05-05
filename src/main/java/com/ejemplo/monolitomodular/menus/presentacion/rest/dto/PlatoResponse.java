package com.ejemplo.monolitomodular.menus.presentacion.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PlatoResponse(
        UUID id,
        String nombre,
        String descripcion,
        BigDecimal precioBase,
        boolean activo
) {
}
