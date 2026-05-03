package com.ejemplo.monolitomodular.menus.aplicacion.dto;

import java.util.List;
import java.util.UUID;

public record ConfigurarMenuCommand(
        UUID reservaRaizId,
        UUID usuarioId,
        String notasGenerales,
        List<SeleccionMenuCommand> selecciones
) {
}
