package com.ejemplo.monolitomodular.menus.aplicacion.dto;

import java.util.UUID;

public record ItemMenuCommand(
        UUID platoId,
        int cantidad,
        String excepciones
) {
}
