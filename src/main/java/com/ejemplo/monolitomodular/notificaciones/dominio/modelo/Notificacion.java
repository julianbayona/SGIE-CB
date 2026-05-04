package com.ejemplo.monolitomodular.notificaciones.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Notificacion {

    private static final int MAX_INTENTOS = 3;

    private final UUID id;
    private final UUID eventoId;
    private final UUID tipoNotificacionId;
    private final LocalDateTime fechaProgramada;
    private final LocalDateTime fechaEnvio;
    private final EstadoNotificacion estado;
    private final int intentos;
    private final String payloadJson;
    private final List<NotificacionDestinatario> destinatarios;

    private Notificacion(
            UUID id,
            UUID eventoId,
            UUID tipoNotificacionId,
            LocalDateTime fechaProgramada,
            LocalDateTime fechaEnvio,
            EstadoNotificacion estado,
            int intentos,
            String payloadJson,
            List<NotificacionDestinatario> destinatarios
    ) {
        this.id = Objects.requireNonNull(id, "El id de la notificacion es obligatorio");
        this.eventoId = eventoId;
        this.tipoNotificacionId = Objects.requireNonNull(tipoNotificacionId, "El tipo de notificacion es obligatorio");
        this.fechaProgramada = Objects.requireNonNull(fechaProgramada, "La fecha programada es obligatoria");
        this.fechaEnvio = fechaEnvio;
        this.estado = Objects.requireNonNull(estado, "El estado de la notificacion es obligatorio");
        if (intentos < 0) {
            throw new DomainException("Los intentos de envio no pueden ser negativos");
        }
        this.intentos = intentos;
        this.payloadJson = payloadJson == null || payloadJson.isBlank() ? "{}" : payloadJson.trim();
        if (destinatarios == null || destinatarios.isEmpty()) {
            throw new DomainException("La notificacion debe tener al menos un destinatario");
        }
        this.destinatarios = List.copyOf(destinatarios);
    }

    public static Notificacion programar(
            UUID eventoId,
            UUID tipoNotificacionId,
            LocalDateTime fechaProgramada,
            String payloadJson,
            List<DestinatarioNuevo> destinatarios
    ) {
        UUID notificacionId = UUID.randomUUID();
        List<NotificacionDestinatario> destinatariosNotificacion = destinatarios.stream()
                .map(destinatario -> NotificacionDestinatario.nuevo(
                        notificacionId,
                        destinatario.usuarioId(),
                        destinatario.telefono()
                ))
                .toList();
        return new Notificacion(
                notificacionId,
                eventoId,
                tipoNotificacionId,
                fechaProgramada,
                null,
                EstadoNotificacion.PENDIENTE,
                0,
                payloadJson,
                destinatariosNotificacion
        );
    }

    public static Notificacion reconstruir(
            UUID id,
            UUID eventoId,
            UUID tipoNotificacionId,
            LocalDateTime fechaProgramada,
            LocalDateTime fechaEnvio,
            EstadoNotificacion estado,
            int intentos,
            String payloadJson,
            List<NotificacionDestinatario> destinatarios
    ) {
        return new Notificacion(id, eventoId, tipoNotificacionId, fechaProgramada, fechaEnvio, estado, intentos, payloadJson, destinatarios);
    }

    public Notificacion iniciarEnvio() {
        if (estado != EstadoNotificacion.PENDIENTE && estado != EstadoNotificacion.ERROR) {
            throw new DomainException("Solo se pueden procesar notificaciones pendientes o con error");
        }
        if (intentos >= MAX_INTENTOS) {
            throw new DomainException("La notificacion ya alcanzo el maximo de intentos");
        }
        return new Notificacion(
                id,
                eventoId,
                tipoNotificacionId,
                fechaProgramada,
                fechaEnvio,
                EstadoNotificacion.ENVIANDO,
                intentos + 1,
                payloadJson,
                destinatarios
        );
    }

    public Notificacion marcarEnviada() {
        return new Notificacion(
                id,
                eventoId,
                tipoNotificacionId,
                fechaProgramada,
                LocalDateTime.now(),
                EstadoNotificacion.ENVIADA,
                intentos,
                payloadJson,
                destinatarios
        );
    }

    public Notificacion marcarError(List<NotificacionDestinatario> destinatariosActualizados) {
        return new Notificacion(
                id,
                eventoId,
                tipoNotificacionId,
                fechaProgramada,
                fechaEnvio,
                EstadoNotificacion.ERROR,
                intentos,
                payloadJson,
                destinatariosActualizados
        );
    }

    public Notificacion finalizarProcesamiento(List<NotificacionDestinatario> destinatariosActualizados) {
        boolean todosEnviados = destinatariosActualizados.stream()
                .allMatch(destinatario -> destinatario.getEstado() == EstadoDestinatarioNotificacion.ENVIADO);
        return new Notificacion(
                id,
                eventoId,
                tipoNotificacionId,
                fechaProgramada,
                todosEnviados ? LocalDateTime.now() : fechaEnvio,
                todosEnviados ? EstadoNotificacion.ENVIADA : EstadoNotificacion.ERROR,
                intentos,
                payloadJson,
                destinatariosActualizados
        );
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

    public List<NotificacionDestinatario> getDestinatarios() {
        return destinatarios;
    }

    public record DestinatarioNuevo(UUID usuarioId, String telefono) {
    }
}
