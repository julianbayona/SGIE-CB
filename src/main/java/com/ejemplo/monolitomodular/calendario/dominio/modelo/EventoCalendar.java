package com.ejemplo.monolitomodular.calendario.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class EventoCalendar {

    private final UUID id;
    private final OrigenEventoCalendar origenTipo;
    private final UUID origenId;
    private final UUID eventoId;
    private final TipoOperacionCalendar tipo;
    private final String googleEventId;
    private final LocalDateTime fechaSync;
    private final EstadoEventoCalendar estado;
    private final String payloadJson;
    private final int intentos;
    private final String mensajeError;

    private EventoCalendar(
            UUID id,
            OrigenEventoCalendar origenTipo,
            UUID origenId,
            UUID eventoId,
            TipoOperacionCalendar tipo,
            String googleEventId,
            LocalDateTime fechaSync,
            EstadoEventoCalendar estado,
            String payloadJson,
            int intentos,
            String mensajeError
    ) {
        this.id = Objects.requireNonNull(id, "El id del evento de calendario es obligatorio");
        this.origenTipo = Objects.requireNonNull(origenTipo, "El origen del evento de calendario es obligatorio");
        this.origenId = Objects.requireNonNull(origenId, "El id de origen del evento de calendario es obligatorio");
        this.eventoId = Objects.requireNonNull(eventoId, "El evento asociado es obligatorio");
        this.tipo = Objects.requireNonNull(tipo, "El tipo de operacion de calendario es obligatorio");
        this.googleEventId = normalizarGoogleEventId(googleEventId);
        this.fechaSync = fechaSync;
        this.estado = Objects.requireNonNull(estado, "El estado del evento de calendario es obligatorio");
        this.payloadJson = payloadJson == null || payloadJson.isBlank() ? "{}" : payloadJson.trim();
        if (intentos < 0) {
            throw new DomainException("Los intentos de sincronizacion no pueden ser negativos");
        }
        this.intentos = intentos;
        this.mensajeError = normalizarMensajeError(mensajeError);
    }

    public static EventoCalendar pendiente(
            OrigenEventoCalendar origenTipo,
            UUID origenId,
            UUID eventoId,
            TipoOperacionCalendar tipo,
            String payloadJson
    ) {
        return new EventoCalendar(
                UUID.randomUUID(),
                origenTipo,
                origenId,
                eventoId,
                tipo,
                null,
                null,
                EstadoEventoCalendar.PENDIENTE,
                payloadJson,
                0,
                null
        );
    }

    public static EventoCalendar reconstruir(
            UUID id,
            OrigenEventoCalendar origenTipo,
            UUID origenId,
            UUID eventoId,
            TipoOperacionCalendar tipo,
            String googleEventId,
            LocalDateTime fechaSync,
            EstadoEventoCalendar estado,
            String payloadJson,
            int intentos,
            String mensajeError
    ) {
        return new EventoCalendar(
                id,
                origenTipo,
                origenId,
                eventoId,
                tipo,
                googleEventId,
                fechaSync,
                estado,
                payloadJson,
                intentos,
                mensajeError
        );
    }

    public EventoCalendar marcarSincronizado(String googleEventId) {
        if (estado == EstadoEventoCalendar.CANCELADO) {
            throw new DomainException("No se puede sincronizar un evento de calendario cancelado");
        }
        return new EventoCalendar(
                id,
                origenTipo,
                origenId,
                eventoId,
                tipo,
                googleEventId,
                LocalDateTime.now(),
                EstadoEventoCalendar.SINCRONIZADO,
                payloadJson,
                intentos,
                null
        );
    }

    public EventoCalendar iniciarIntento() {
        if (estado != EstadoEventoCalendar.PENDIENTE && estado != EstadoEventoCalendar.ERROR) {
            throw new DomainException("Solo se pueden procesar eventos de calendario pendientes o con error");
        }
        return new EventoCalendar(
                id,
                origenTipo,
                origenId,
                eventoId,
                tipo,
                googleEventId,
                fechaSync,
                estado,
                payloadJson,
                intentos + 1,
                mensajeError
        );
    }

    public EventoCalendar marcarError(String mensajeError) {
        if (estado == EstadoEventoCalendar.CANCELADO) {
            return this;
        }
        return new EventoCalendar(
                id,
                origenTipo,
                origenId,
                eventoId,
                tipo,
                googleEventId,
                fechaSync,
                EstadoEventoCalendar.ERROR,
                payloadJson,
                intentos,
                mensajeError
        );
    }

    public UUID getId() {
        return id;
    }

    public OrigenEventoCalendar getOrigenTipo() {
        return origenTipo;
    }

    public UUID getOrigenId() {
        return origenId;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public TipoOperacionCalendar getTipo() {
        return tipo;
    }

    public String getGoogleEventId() {
        return googleEventId;
    }

    public LocalDateTime getFechaSync() {
        return fechaSync;
    }

    public EstadoEventoCalendar getEstado() {
        return estado;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public int getIntentos() {
        return intentos;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    private static String normalizarGoogleEventId(String googleEventId) {
        if (googleEventId == null || googleEventId.isBlank()) {
            return null;
        }
        String valor = googleEventId.trim();
        if (valor.length() > 255) {
            throw new DomainException("El identificador de Google Calendar no es valido");
        }
        return valor;
    }

    private static String normalizarMensajeError(String mensajeError) {
        if (mensajeError == null || mensajeError.isBlank()) {
            return null;
        }
        String valor = mensajeError.trim();
        return valor.length() <= 1000 ? valor : valor.substring(0, 1000);
    }
}
