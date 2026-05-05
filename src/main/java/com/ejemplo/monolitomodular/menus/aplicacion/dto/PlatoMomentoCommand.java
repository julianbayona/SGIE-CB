package com.ejemplo.monolitomodular.menus.aplicacion.dto;

import java.util.UUID;

public record PlatoMomentoCommand(
        UUID platoId,
        UUID tipoMomentoId
) {
}
