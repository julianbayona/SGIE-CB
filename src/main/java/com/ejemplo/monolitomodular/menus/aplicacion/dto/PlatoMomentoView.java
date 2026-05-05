package com.ejemplo.monolitomodular.menus.aplicacion.dto;

import java.util.UUID;

public record PlatoMomentoView(
        UUID platoId,
        UUID tipoMomentoId
) {
}
