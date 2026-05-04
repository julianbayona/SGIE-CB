package com.ejemplo.monolitomodular.notificaciones.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;

public class NotificacionDestinatario {

    private final UUID id;
    private final UUID notificacionId;
    private final UUID usuarioId;
    private final String telefono;
    private final EstadoDestinatarioNotificacion estado;

    private NotificacionDestinatario(
            UUID id,
            UUID notificacionId,
            UUID usuarioId,
            String telefono,
            EstadoDestinatarioNotificacion estado
    ) {
        this.id = Objects.requireNonNull(id, "El id del destinatario es obligatorio");
        this.notificacionId = Objects.requireNonNull(notificacionId, "La notificacion del destinatario es obligatoria");
        this.usuarioId = usuarioId;
        this.telefono = validarTelefono(telefono);
        this.estado = Objects.requireNonNull(estado, "El estado del destinatario es obligatorio");
    }

    public static NotificacionDestinatario nuevo(UUID notificacionId, UUID usuarioId, String telefono) {
        return new NotificacionDestinatario(
                UUID.randomUUID(),
                notificacionId,
                usuarioId,
                telefono,
                EstadoDestinatarioNotificacion.PENDIENTE
        );
    }

    public static NotificacionDestinatario reconstruir(
            UUID id,
            UUID notificacionId,
            UUID usuarioId,
            String telefono,
            EstadoDestinatarioNotificacion estado
    ) {
        return new NotificacionDestinatario(id, notificacionId, usuarioId, telefono, estado);
    }

    public NotificacionDestinatario marcarEnviado() {
        return new NotificacionDestinatario(id, notificacionId, usuarioId, telefono, EstadoDestinatarioNotificacion.ENVIADO);
    }

    public NotificacionDestinatario marcarError() {
        return new NotificacionDestinatario(id, notificacionId, usuarioId, telefono, EstadoDestinatarioNotificacion.ERROR);
    }

    private static String validarTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new DomainException("El telefono del destinatario es obligatorio");
        }
        return telefono.trim();
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
