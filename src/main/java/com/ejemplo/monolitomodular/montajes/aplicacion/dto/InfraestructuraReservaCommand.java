package com.ejemplo.monolitomodular.montajes.aplicacion.dto;

public record InfraestructuraReservaCommand(
        boolean mesaPonque,
        boolean mesaRegalos,
        boolean espacioMusicos,
        boolean estanteBombas
) {
}
