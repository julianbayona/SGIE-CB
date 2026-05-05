package com.ejemplo.monolitomodular.menus.aplicacion.dto;

import java.math.BigDecimal;

public record PlatoCommand(
        String nombre,
        String descripcion,
        BigDecimal precioBase
) {
}
