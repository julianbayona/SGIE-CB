package com.ejemplo.monolitomodular.menus.aplicacion.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PlatoView(
        UUID id,
        String nombre,
        String descripcion,
        BigDecimal precioBase,
        boolean activo
) {
}
