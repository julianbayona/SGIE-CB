package com.ejemplo.monolitomodular.montajes.presentacion.rest.dto;

public record InfraestructuraReservaRequest(
        boolean mesaPonque,
        boolean mesaRegalos,
        boolean espacioMusicos,
        boolean estanteBombas
) {
}
