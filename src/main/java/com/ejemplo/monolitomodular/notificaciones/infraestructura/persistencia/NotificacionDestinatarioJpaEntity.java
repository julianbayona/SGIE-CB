package com.ejemplo.monolitomodular.notificaciones.infraestructura.persistencia;

import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoDestinatarioNotificacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "notificacion_destinatario")
public class NotificacionDestinatarioJpaEntity {

    @Id
    @Column(name = "id_notificacion_destinatario")
    private UUID id;

    @Column(name = "id_notificacion", nullable = false)
    private UUID notificacionId;

    @Column(name = "id_usuario")
    private UUID usuarioId;

    @Column(name = "telefono", nullable = false, length = 30)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 40)
    private EstadoDestinatarioNotificacion estado;

    protected NotificacionDestinatarioJpaEntity() {
    }

    public NotificacionDestinatarioJpaEntity(
            UUID id,
            UUID notificacionId,
            UUID usuarioId,
            String telefono,
            EstadoDestinatarioNotificacion estado
    ) {
        this.id = id;
        this.notificacionId = notificacionId;
        this.usuarioId = usuarioId;
        this.telefono = telefono;
        this.estado = estado;
    }

    public UUID getId() {
        return id;
    }

    public UUID getNotificacionId() {
        return notificacionId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getTelefono() {
        return telefono;
    }

    public EstadoDestinatarioNotificacion getEstado() {
        return estado;
    }
}
