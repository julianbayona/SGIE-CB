package com.ejemplo.monolitomodular.eventos.presentacion.rest.dto;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EventoResponse(
        UUID id,
        UUID clienteId,
        UUID tipoEventoId,
        UUID tipoComidaId,
        UUID usuarioCreadorId,
        EstadoEvento estado,
        String gcalEventId,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        List<ReservaSalonResponse> reservas
) {
}
