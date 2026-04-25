package com.ejemplo.monolitomodular.eventos.presentacion.rest.dto;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record EventoResponse(
        UUID id,
        UUID clienteId,
        String tipoEvento,
        String tipoComida,
        LocalDate fechaEvento,
        LocalTime horaInicio,
        LocalTime horaFin,
        int numeroPersonas,
        EstadoEvento estado,
        String observaciones,
        List<UUID> salonIds
) {
}
