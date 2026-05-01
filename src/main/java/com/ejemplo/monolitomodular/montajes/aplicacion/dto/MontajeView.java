package com.ejemplo.monolitomodular.montajes.aplicacion.dto;

import java.util.List;
import java.util.UUID;

public record MontajeView(
        UUID id,
        UUID reservaId,
        String observaciones,
        List<MontajeMesaReservaView> mesas,
        InfraestructuraReservaView infraestructura
) {
}
