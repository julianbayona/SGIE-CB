package com.ejemplo.monolitomodular.eventos.dominio.modelo;

import java.time.LocalDateTime;
import java.util.UUID;

public class HistorialEstadoEvento {

    private final UUID id;
    private final UUID eventoId;
    private final UUID usuarioId;
    private final EstadoEvento estadoAnterior;
    private final EstadoEvento estadoNuevo;
    private final String observacion;
    private final LocalDateTime fechaCambio;

    private HistorialEstadoEvento(
            UUID id,
            UUID eventoId,
            UUID usuarioId,
            EstadoEvento estadoAnterior,
            EstadoEvento estadoNuevo,
            String observacion,
            LocalDateTime fechaCambio
    ) {
        this.id = id;
        this.eventoId = eventoId;
        this.usuarioId = usuarioId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.observacion = observacion == null ? "" : observacion.trim();
        this.fechaCambio = fechaCambio;
    }

    public static HistorialEstadoEvento registrarCreacion(UUID eventoId, UUID usuarioId) {
        return new HistorialEstadoEvento(
                UUID.randomUUID(),
                eventoId,
                usuarioId,
                null,
                EstadoEvento.PENDIENTE,
                "Creacion del evento",
                LocalDateTime.now()
        );
    }

    public static HistorialEstadoEvento reconstruir(
            UUID id,
            UUID eventoId,
            UUID usuarioId,
            EstadoEvento estadoAnterior,
            EstadoEvento estadoNuevo,
            String observacion,
            LocalDateTime fechaCambio
    ) {
        return new HistorialEstadoEvento(id, eventoId, usuarioId, estadoAnterior, estadoNuevo, observacion, fechaCambio);
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

    public String getObservacion() {
        return observacion;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }
}
