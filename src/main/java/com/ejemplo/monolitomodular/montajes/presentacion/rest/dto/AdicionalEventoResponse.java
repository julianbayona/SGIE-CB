package com.ejemplo.monolitomodular.montajes.presentacion.rest.dto;

import java.util.UUID;

public record AdicionalEventoResponse(
        UUID id,
        UUID tipoAdicionalId,
        int cantidad
) {
}
