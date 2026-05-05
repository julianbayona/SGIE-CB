package com.ejemplo.monolitomodular.notificaciones.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;

public class NotificacionDestinatario {

    private final UUID id;
    private final UUID notificacionId;
    private final UUID usuarioId;
    private final String telefono;
    private final String correo;
    private final EstadoDestinatarioNotificacion estado;

    private NotificacionDestinatario(
            UUID id,
            UUID notificacionId,
            UUID usuarioId,
            String telefono,
            String correo,
            EstadoDestinatarioNotificacion estado
    ) {
        this.id = Objects.requireNonNull(id, "El id del destinatario es obligatorio");
        this.notificacionId = Objects.requireNonNull(notificacionId, "La notificacion del destinatario es obligatoria");
        this.usuarioId = usuarioId;
        this.telefono = normalizar(telefono);
        this.correo = normalizar(correo);
        if (this.telefono == null && this.correo == null) {
            throw new DomainException("El destinatario debe tener telefono o correo");
        }
        this.estado = Objects.requireNonNull(estado, "El estado del destinatario es obligatorio");
    }

    public static NotificacionDestinatario nuevo(UUID notificacionId, UUID usuarioId, String telefono, String correo) {
        return new NotificacionDestinatario(
                UUID.randomUUID(),
                notificacionId,
                usuarioId,
                telefono,
                correo,
                EstadoDestinatarioNotificacion.PENDIENTE
        );
    }

    public static NotificacionDestinatario reconstruir(
            UUID id,
            UUID notificacionId,
            UUID usuarioId,
            String telefono,
            String correo,
            EstadoDestinatarioNotificacion estado
    ) {
        return new NotificacionDestinatario(id, notificacionId, usuarioId, telefono, correo, estado);
    }

    public NotificacionDestinatario marcarEnviado() {
        return new NotificacionDestinatario(id, notificacionId, usuarioId, telefono, correo, EstadoDestinatarioNotificacion.ENVIADO);
    }

    public NotificacionDestinatario marcarError() {
        return new NotificacionDestinatario(id, notificacionId, usuarioId, telefono, correo, EstadoDestinatarioNotificacion.ERROR);
    }

    private static String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
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

    public String getCorreo() {
        return correo;
    }

    public boolean tieneTelefono() {
        return telefono != null;
    }

    public boolean tieneCorreo() {
        return correo != null;
    }

    public EstadoDestinatarioNotificacion getEstado() {
        return estado;
    }
}
