package com.ejemplo.monolitomodular.eventos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_estado_evento")
public class HistorialEstadoEventoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "evento_id", nullable = false)
    private UUID eventoId;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 40)
    private EstadoEvento estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 40)
    private EstadoEvento estadoNuevo;

    @Column(length = 255)
    private String observacion;

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    protected HistorialEstadoEventoJpaEntity() {
    }

    public HistorialEstadoEventoJpaEntity(
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
        this.observacion = observacion;
        this.fechaCambio = fechaCambio;
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
