package com.ejemplo.monolitomodular.montajes.presentacion.rest.dto;

import java.util.List;
import java.util.UUID;

public record MontajeResponse(
        UUID id,
        UUID reservaId,
        String observaciones,
        List<MontajeMesaReservaResponse> mesas,
        InfraestructuraReservaResponse infraestructura,
        List<AdicionalEventoResponse> adicionales
) {
}
