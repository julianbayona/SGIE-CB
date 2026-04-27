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
@Table(name = "evento")
public class EventoJpaEntity {

    @Id
    @Column(name = "id_evento")
    private UUID id;

    @Column(name = "id_cliente", nullable = false)
    private UUID clienteId;

    @Column(name = "id_tipo_evento", nullable = false)
    private UUID tipoEventoId;

    @Column(name = "id_tipo_comida", nullable = false)
    private UUID tipoComidaId;

    @Column(name = "id_usuario_creador", nullable = false)
    private UUID usuarioCreadorId;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EstadoEvento estado;

    @Column(name = "gcal_event_id", length = 255)
    private String gcalEventId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected EventoJpaEntity() {
    }

    public EventoJpaEntity(
            UUID id,
            UUID clienteId,
            UUID tipoEventoId,
            UUID tipoComidaId,
            UUID usuarioCreadorId,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            EstadoEvento estado,
            String gcalEventId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.clienteId = clienteId;
        this.tipoEventoId = tipoEventoId;
        this.tipoComidaId = tipoComidaId;
        this.usuarioCreadorId = usuarioCreadorId;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
        this.gcalEventId = gcalEventId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public UUID getTipoEventoId() {
        return tipoEventoId;
    }

    public UUID getTipoComidaId() {
        return tipoComidaId;
    }

    public UUID getUsuarioCreadorId() {
        return usuarioCreadorId;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public String getGcalEventId() {
        return gcalEventId;
    }
}
