package com.ejemplo.monolitomodular.notificaciones.infraestructura.persistencia;

import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoNotificacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacion")
public class NotificacionJpaEntity {

    @Id
    @Column(name = "id_notificacion")
    private UUID id;

    @Column(name = "id_evento")
    private UUID eventoId;

    @Column(name = "id_tipo_notificacion", nullable = false)
    private UUID tipoNotificacionId;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 40)
    private EstadoNotificacion estado;

    @Column(name = "intentos", nullable = false)
    private int intentos;

    @Lob
    @Column(name = "payload_json", nullable = false)
    private String payloadJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected NotificacionJpaEntity() {
    }

    public NotificacionJpaEntity(
            UUID id,
            UUID eventoId,
            UUID tipoNotificacionId,
            LocalDateTime fechaProgramada,
            LocalDateTime fechaEnvio,
            EstadoNotificacion estado,
            int intentos,
            String payloadJson,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.eventoId = eventoId;
        this.tipoNotificacionId = tipoNotificacionId;
        this.fechaProgramada = fechaProgramada;
        this.fechaEnvio = fechaEnvio;
        this.estado = estado;
        this.intentos = intentos;
        this.payloadJson = payloadJson;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public UUID getTipoNotificacionId() {
        return tipoNotificacionId;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public EstadoNotificacion getEstado() {
        return estado;
    }

    public int getIntentos() {
        return intentos;
    }

    public String getPayloadJson() {
        return payloadJson;
    }
}
