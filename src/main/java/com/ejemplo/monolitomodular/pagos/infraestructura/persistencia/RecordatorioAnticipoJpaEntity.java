package com.ejemplo.monolitomodular.pagos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.pagos.dominio.modelo.EstadoRecordatorioAnticipo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "recordatorio_anticipo")
public class RecordatorioAnticipoJpaEntity {

    @Id
    @Column(name = "id_recordatorio_anticipo")
    private UUID id;

    @Column(name = "id_evento", nullable = false)
    private UUID eventoId;

    @Column(name = "id_usuario", nullable = false)
    private UUID usuarioId;

    @Column(name = "fecha_recordatorio", nullable = false)
    private LocalDate fechaRecordatorio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 40)
    private EstadoRecordatorioAnticipo estado;

    @Column(name = "id_notificacion")
    private UUID notificacionId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected RecordatorioAnticipoJpaEntity() {
    }

    public RecordatorioAnticipoJpaEntity(
            UUID id,
            UUID eventoId,
            UUID usuarioId,
            LocalDate fechaRecordatorio,
            EstadoRecordatorioAnticipo estado,
            UUID notificacionId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.eventoId = eventoId;
        this.usuarioId = usuarioId;
        this.fechaRecordatorio = fechaRecordatorio;
        this.estado = estado;
        this.notificacionId = notificacionId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public LocalDate getFechaRecordatorio() {
        return fechaRecordatorio;
    }

    public EstadoRecordatorioAnticipo getEstado() {
        return estado;
    }

    public UUID getNotificacionId() {
        return notificacionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
