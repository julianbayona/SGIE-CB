package com.ejemplo.monolitomodular.eventos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Evento {

    private final UUID id;
    private final UUID clienteId;
    private final UUID tipoEventoId;
    private final UUID tipoComidaId;
    private final UUID usuarioCreadorId;
    private final LocalDateTime fechaHoraInicio;
    private final LocalDateTime fechaHoraFin;
    private final EstadoEvento estado;
    private final String gcalEventId;

    private Evento(
            UUID id,
            UUID clienteId,
            UUID tipoEventoId,
            UUID tipoComidaId,
            UUID usuarioCreadorId,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            EstadoEvento estado,
            String gcalEventId
    ) {
        this.id = Objects.requireNonNull(id, "El id del evento es obligatorio");
        this.clienteId = Objects.requireNonNull(clienteId, "El cliente del evento es obligatorio");
        this.tipoEventoId = Objects.requireNonNull(tipoEventoId, "El tipo de evento es obligatorio");
        this.tipoComidaId = Objects.requireNonNull(tipoComidaId, "El tipo de comida es obligatorio");
        this.usuarioCreadorId = Objects.requireNonNull(usuarioCreadorId, "El usuario creador es obligatorio");
        this.fechaHoraInicio = Objects.requireNonNull(fechaHoraInicio, "La fecha y hora de inicio del evento es obligatoria");
        this.fechaHoraFin = Objects.requireNonNull(fechaHoraFin, "La fecha y hora de fin del evento es obligatoria");
        if (!fechaHoraFin.isAfter(fechaHoraInicio)) {
            throw new DomainException("La fecha y hora de fin debe ser posterior a la fecha y hora de inicio");
        }
        this.estado = Objects.requireNonNull(estado, "El estado del evento es obligatorio");
        this.gcalEventId = normalizarGcalEventId(gcalEventId);
    }

    public static Evento nuevo(
            UUID clienteId,
            UUID tipoEventoId,
            UUID tipoComidaId,
            UUID usuarioCreadorId,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin
    ) {
        return new Evento(
                UUID.randomUUID(),
                clienteId,
                tipoEventoId,
                tipoComidaId,
                usuarioCreadorId,
                fechaHoraInicio,
                fechaHoraFin,
                EstadoEvento.PENDIENTE,
                null
        );
    }

    public static Evento reconstruir(
            UUID id,
            UUID clienteId,
            UUID tipoEventoId,
            UUID tipoComidaId,
            UUID usuarioCreadorId,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            EstadoEvento estado,
            String gcalEventId
    ) {
        return new Evento(id, clienteId, tipoEventoId, tipoComidaId, usuarioCreadorId, fechaHoraInicio, fechaHoraFin, estado, gcalEventId);
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

    private static String normalizarGcalEventId(String gcalEventId) {
        if (gcalEventId == null || gcalEventId.isBlank()) {
            return null;
        }
        String valor = gcalEventId.trim();
        if (valor.length() > 255) {
            throw new DomainException("El identificador de Google Calendar no es valido");
        }
        return valor;
    }
}
