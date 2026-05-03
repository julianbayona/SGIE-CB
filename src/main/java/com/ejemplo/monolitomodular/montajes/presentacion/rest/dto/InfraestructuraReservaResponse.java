package com.ejemplo.monolitomodular.montajes.presentacion.rest.dto;

import java.util.UUID;

public record InfraestructuraReservaResponse(
        UUID id,
        boolean mesaPonque,
        boolean mesaRegalos,
        boolean espacioMusicos,
        boolean estanteBombas
) {
}
