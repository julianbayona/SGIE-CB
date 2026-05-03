package com.ejemplo.monolitomodular.menus.aplicacion.dto;

import java.util.List;
import java.util.UUID;

public record MenuView(
        UUID id,
        UUID reservaId,
        String notasGenerales,
        List<SeleccionMenuView> selecciones
) {
}
