package com.ejemplo.monolitomodular.catalogos.aplicacion.dto;

import java.math.BigDecimal;

public record PlatoView(
        String id,
        String nombre,
        String descripcion,
        BigDecimal precioBase,
        boolean activo
) {
}
