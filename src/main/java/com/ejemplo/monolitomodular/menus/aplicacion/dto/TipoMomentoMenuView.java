package com.ejemplo.monolitomodular.menus.aplicacion.dto;

import java.util.UUID;

public record TipoMomentoMenuView(
        UUID id,
        String nombre,
        boolean activo
) {
}
