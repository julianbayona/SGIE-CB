package com.ejemplo.monolitomodular.montajes.aplicacion.dto;

import java.util.List;
import java.util.UUID;

public record ConfigurarMontajeCommand(
        UUID reservaRaizId,
        UUID usuarioId,
        String observaciones,
        List<MontajeMesaReservaCommand> mesas,
        InfraestructuraReservaCommand infraestructura,
        List<AdicionalEventoCommand> adicionales
) {
}
