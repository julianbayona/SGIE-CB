package com.ejemplo.monolitomodular.eventos.dominio.modelo;

import java.time.LocalDateTime;
import java.util.UUID;

public class HistorialEstadoEvento {

    private final UUID id;
    private final UUID eventoId;
    private final UUID usuarioId;
    private final EstadoEvento estadoAnterior;
    private final EstadoEvento estadoNuevo;
    private final LocalDateTime createdAt;

    private HistorialEstadoEvento(
            UUID id,
            UUID eventoId,
            UUID usuarioId,
            EstadoEvento estadoAnterior,
            EstadoEvento estadoNuevo,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.eventoId = eventoId;
        this.usuarioId = usuarioId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.createdAt = createdAt;
    }

    public static HistorialEstadoEvento registrarCreacion(UUID eventoId, UUID usuarioId) {
        return new HistorialEstadoEvento(
                UUID.randomUUID(),
                eventoId,
                usuarioId,
                null,
                EstadoEvento.PENDIENTE,
                LocalDateTime.now()
        );
    }

    public static HistorialEstadoEvento reconstruir(
            UUID id,
            UUID eventoId,
            UUID usuarioId,
            EstadoEvento estadoAnterior,
            EstadoEvento estadoNuevo,
            LocalDateTime createdAt
    ) {
        return new HistorialEstadoEvento(id, eventoId, usuarioId, estadoAnterior, estadoNuevo, createdAt);
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public EstadoEvento getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoEvento getEstadoNuevo() {
        return estadoNuevo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
