package com.ejemplo.monolitomodular.eventos.aplicacion.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record CrearEventoCommand(
        UUID clienteId,
        String tipoEvento,
        String tipoComida,
        LocalDate fechaEvento,
        LocalTime horaInicio,
        int duracionHoras,
        int numeroPersonas,
        List<UUID> salonIds,
        String observaciones,
        UUID usuarioResponsableId
) {
}
