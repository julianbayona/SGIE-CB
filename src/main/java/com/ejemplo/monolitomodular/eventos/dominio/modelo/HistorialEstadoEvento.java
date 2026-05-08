package com.ejemplo.monolitomodular.eventos.dominio.modelo;

import java.time.LocalDateTime;
import java.util.UUID;

public class HistorialEstadoEvento {

    private final UUID id;
    private final UUID eventoId;
    private final UUID usuarioId;
    private final EstadoEvento estadoAnterior;
    private final EstadoEvento estadoNuevo;
    private final String motivo;
    private final LocalDateTime createdAt;

    private HistorialEstadoEvento(
            UUID id,
            UUID eventoId,
            UUID usuarioId,
            EstadoEvento estadoAnterior,
            EstadoEvento estadoNuevo,
            String motivo,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.eventoId = eventoId;
        this.usuarioId = usuarioId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.motivo = normalizarMotivo(motivo);
        this.createdAt = createdAt;
    }

    public static HistorialEstadoEvento registrarCreacion(UUID eventoId, UUID usuarioId) {
        return new HistorialEstadoEvento(
                UUID.randomUUID(),
                eventoId,
                usuarioId,
                null,
                EstadoEvento.PENDIENTE,
                null,
                LocalDateTime.now()
        );
    }

    public static HistorialEstadoEvento registrarCambio(
            UUID eventoId,
            UUID usuarioId,
            EstadoEvento estadoAnterior,
            EstadoEvento estadoNuevo
    ) {
        return new HistorialEstadoEvento(
                UUID.randomUUID(),
                eventoId,
                usuarioId,
                estadoAnterior,
                estadoNuevo,
                null,
                LocalDateTime.now()
        );
    }

    public static HistorialEstadoEvento registrarCambioConMotivo(
            UUID eventoId,
            UUID usuarioId,
            EstadoEvento estadoAnterior,
            EstadoEvento estadoNuevo,
            String motivo
    ) {
        return new HistorialEstadoEvento(
                UUID.randomUUID(),
                eventoId,
                usuarioId,
                estadoAnterior,
                estadoNuevo,
                motivo,
                LocalDateTime.now()
        );
    }

    public static HistorialEstadoEvento reconstruir(
            UUID id,
            UUID eventoId,
            UUID usuarioId,
            EstadoEvento estadoAnterior,
            EstadoEvento estadoNuevo,
            String motivo,
            LocalDateTime createdAt
    ) {
        return new HistorialEstadoEvento(id, eventoId, usuarioId, estadoAnterior, estadoNuevo, motivo, createdAt);
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

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    private static String normalizarMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            return null;
        }
        String valor = motivo.trim();
        return valor.length() <= 500 ? valor : valor.substring(0, 500);
    }
}
