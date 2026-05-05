package com.ejemplo.monolitomodular.pagos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class RecordatorioAnticipo {

    private final UUID id;
    private final UUID eventoId;
    private final UUID usuarioId;
    private final LocalDate fechaRecordatorio;
    private final EstadoRecordatorioAnticipo estado;
    private final UUID notificacionId;

    private RecordatorioAnticipo(
            UUID id,
            UUID eventoId,
            UUID usuarioId,
            LocalDate fechaRecordatorio,
            EstadoRecordatorioAnticipo estado,
            UUID notificacionId,
            boolean validarFechaFutura
    ) {
        this.id = Objects.requireNonNull(id, "El id del recordatorio es obligatorio");
        this.eventoId = Objects.requireNonNull(eventoId, "El evento del recordatorio es obligatorio");
        this.usuarioId = Objects.requireNonNull(usuarioId, "El usuario del recordatorio es obligatorio");
        this.fechaRecordatorio = validarFechaFutura
                ? validarFechaFutura(fechaRecordatorio)
                : Objects.requireNonNull(fechaRecordatorio, "La fecha del recordatorio es obligatoria");
        this.estado = Objects.requireNonNull(estado, "El estado del recordatorio es obligatorio");
        this.notificacionId = notificacionId;
    }

    public static RecordatorioAnticipo programar(UUID eventoId, UUID usuarioId, LocalDate fechaRecordatorio) {
        return new RecordatorioAnticipo(
                UUID.randomUUID(),
                eventoId,
                usuarioId,
                fechaRecordatorio,
                EstadoRecordatorioAnticipo.PENDIENTE,
                null,
                true
        );
    }

    public static RecordatorioAnticipo reconstruir(
            UUID id,
            UUID eventoId,
            UUID usuarioId,
            LocalDate fechaRecordatorio,
            EstadoRecordatorioAnticipo estado,
            UUID notificacionId
    ) {
        return new RecordatorioAnticipo(id, eventoId, usuarioId, fechaRecordatorio, estado, notificacionId, false);
    }

    public RecordatorioAnticipo marcarNotificacionCreada(UUID notificacionId) {
        if (estado != EstadoRecordatorioAnticipo.PENDIENTE) {
            throw new DomainException("Solo un recordatorio pendiente puede crear notificacion");
        }
        return new RecordatorioAnticipo(
                id,
                eventoId,
                usuarioId,
                fechaRecordatorio,
                EstadoRecordatorioAnticipo.NOTIFICACION_CREADA,
                Objects.requireNonNull(notificacionId, "La notificacion creada es obligatoria"),
                false
        );
    }

    public RecordatorioAnticipo omitir() {
        if (estado != EstadoRecordatorioAnticipo.PENDIENTE) {
            return this;
        }
        return new RecordatorioAnticipo(
                id,
                eventoId,
                usuarioId,
                fechaRecordatorio,
                EstadoRecordatorioAnticipo.OMITIDO,
                notificacionId,
                false
        );
    }

    private static LocalDate validarFechaFutura(LocalDate fechaRecordatorio) {
        LocalDate fecha = Objects.requireNonNull(fechaRecordatorio, "La fecha del recordatorio es obligatoria");
        if (fecha.isBefore(LocalDate.now())) {
            throw new DomainException("La fecha del recordatorio no puede estar en el pasado");
        }
        return fecha;
    }

    public boolean estaVencido(LocalDate fechaReferencia) {
        return estado == EstadoRecordatorioAnticipo.PENDIENTE
                && !fechaRecordatorio.isAfter(fechaReferencia);
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
}
