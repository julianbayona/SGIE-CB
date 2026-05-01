package com.ejemplo.monolitomodular.montajes.aplicacion.dto;

import java.util.UUID;

public record MontajeMesaReservaView(
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
