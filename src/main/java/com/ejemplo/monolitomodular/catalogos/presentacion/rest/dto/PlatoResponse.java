package com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto;

import java.math.BigDecimal;

public record PlatoResponse(
        String id,
        String nombre,
        String descripcion,
        BigDecimal precioBase,
        boolean activo
) {
}
