package com.ejemplo.monolitomodular.montajes.aplicacion.dto;

import java.util.UUID;

public record AdicionalEventoCommand(
        UUID tipoAdicionalId,
        int cantidad
) {
}
