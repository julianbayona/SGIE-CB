package com.ejemplo.monolitomodular.montajes.presentacion.rest.dto;

import java.util.UUID;

public record MontajeMesaReservaResponse(
        UUID id,
        UUID tipoMesaId,
        UUID tipoSillaId,
        int sillaPorMesa,
        int cantidadMesas,
        UUID mantelId,
        UUID sobremantelId,
        boolean vajilla,
        boolean fajon
) {
}
