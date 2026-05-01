package com.ejemplo.monolitomodular.menus.presentacion.rest.dto;

import java.util.List;
import java.util.UUID;

public record MenuResponse(
        UUID id,
        UUID reservaId,
        String notasGenerales,
        List<SeleccionMenuResponse> selecciones
) {
}
