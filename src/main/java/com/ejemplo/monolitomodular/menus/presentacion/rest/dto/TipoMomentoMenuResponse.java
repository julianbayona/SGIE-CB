package com.ejemplo.monolitomodular.menus.presentacion.rest.dto;

import java.util.UUID;

public record TipoMomentoMenuResponse(
        UUID id,
        String nombre,
        boolean activo
) {
}
