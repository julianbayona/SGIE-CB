package com.ejemplo.monolitomodular.montajes.aplicacion.dto;

import java.util.UUID;

public record InfraestructuraReservaView(
        UUID id,
        boolean mesaPonque,
        boolean mesaRegalos,
        boolean espacioMusicos,
        boolean estanteBombas
) {
}
