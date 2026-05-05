package com.ejemplo.monolitomodular.menus.presentacion.rest.dto;

import java.util.UUID;

public record PlatoMomentoResponse(
        UUID platoId,
        UUID tipoMomentoId
) {
}
