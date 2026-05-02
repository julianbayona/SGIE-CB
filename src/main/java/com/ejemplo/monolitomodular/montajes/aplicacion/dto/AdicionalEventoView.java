package com.ejemplo.monolitomodular.montajes.aplicacion.dto;

import java.util.UUID;

public record AdicionalEventoView(
        UUID id,
        UUID tipoAdicionalId,
        int cantidad
) {
}
