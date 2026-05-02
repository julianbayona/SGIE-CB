package com.ejemplo.monolitomodular.menus.aplicacion.dto;

import java.util.UUID;

public record ItemMenuView(
        UUID id,
        UUID platoId,
        int cantidad,
        String excepciones
) {
}
