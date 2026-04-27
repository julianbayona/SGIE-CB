package com.ejemplo.monolitomodular.eventos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ReservaSalon {

    private final UUID id;
    private final UUID reservaRaizId;
    private final UUID eventoId;
    private final UUID salonId;
    private final int numInvitados;
    private final LocalDateTime fechaHoraInicio;
    private final LocalDateTime fechaHoraFin;
    private final String estado;
    private final int version;
    private final boolean vigente;
    private final UUID creadoPor;

    private ReservaSalon(
            UUID id,
            UUID reservaRaizId,
            UUID eventoId,
            UUID salonId,
            int numInvitados,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            String estado,
            int version,
            boolean vigente,
            UUID creadoPor
    ) {
        this.id = Objects.requireNonNull(id, "El id de la reserva es obligatorio");
        this.reservaRaizId = Objects.requireNonNull(reservaRaizId, "La raiz de la reserva es obligatoria");
        this.eventoId = Objects.requireNonNull(eventoId, "El evento es obligatorio");
        this.salonId = Objects.requireNonNull(salonId, "El salon es obligatorio");
        if (numInvitados <= 0) {
            throw new DomainException("El numero de invitados debe ser mayor a cero");
        }
        this.numInvitados = numInvitados;
        this.fechaHoraInicio = Objects.requireNonNull(fechaHoraInicio, "La fecha y hora de inicio de la reserva es obligatoria");
        this.fechaHoraFin = Objects.requireNonNull(fechaHoraFin, "La fecha y hora de fin de la reserva es obligatoria");
        if (!fechaHoraFin.isAfter(fechaHoraInicio)) {
            throw new DomainException("La fecha y hora de fin debe ser posterior a la fecha y hora de inicio");
        }
        this.estado = validarEstado(estado);
        if (version <= 0) {
            throw new DomainException("La version de la reserva debe ser mayor a cero");
        }
        this.version = version;
        this.vigente = vigente;
        this.creadoPor = Objects.requireNonNull(creadoPor, "El usuario creador de la reserva es obligatorio");
    }

    public static ReservaSalon nueva(
            UUID eventoId,
            UUID salonId,
            int numInvitados,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            UUID creadoPor
    ) {
        UUID id = UUID.randomUUID();
        return new ReservaSalon(id, id, eventoId, salonId, numInvitados, fechaHoraInicio, fechaHoraFin, "PENDIENTE", 1, true, creadoPor);
    }

    public ReservaSalon crearNuevaVersion(
            UUID salonId,
            int numInvitados,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            UUID creadoPor
    ) {
        return new ReservaSalon(
                UUID.randomUUID(),
                reservaRaizId,
                eventoId,
                salonId,
                numInvitados,
                fechaHoraInicio,
                fechaHoraFin,
                estado,
                version + 1,
                true,
                creadoPor
        );
    }

    public ReservaSalon marcarComoNoVigente() {
        return new ReservaSalon(
                id,
                reservaRaizId,
                eventoId,
                salonId,
                numInvitados,
                fechaHoraInicio,
                fechaHoraFin,
                estado,
                version,
                false,
                creadoPor
        );
    }

    public static ReservaSalon reconstruir(
            UUID id,
            UUID reservaRaizId,
            UUID eventoId,
            UUID salonId,
            int numInvitados,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            String estado,
            int version,
            boolean vigente,
            UUID creadoPor
    ) {
        return new ReservaSalon(id, reservaRaizId, eventoId, salonId, numInvitados, fechaHoraInicio, fechaHoraFin, estado, version, vigente, creadoPor);
    }

    public UUID getId() {
        return id;
    }

    public UUID getReservaRaizId() {
        return reservaRaizId;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public UUID getSalonId() {
        return salonId;
    }

    public int getNumInvitados() {
        return numInvitados;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public String getEstado() {
        return estado;
    }

    public int getVersion() {
        return version;
    }

    public boolean isVigente() {
        return vigente;
    }

    public UUID getCreadoPor() {
        return creadoPor;
    }

    private static String validarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new DomainException("El estado de la reserva es obligatorio");
        }
        return estado.trim().toUpperCase();
    }
}
