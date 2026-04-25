package com.ejemplo.monolitomodular.eventos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ReservaSalon {

    private final UUID id;
    private final UUID eventoId;
    private final UUID salonId;
    private final LocalDateTime fechaInicio;
    private final LocalDateTime fechaFin;

    private ReservaSalon(UUID id, UUID eventoId, UUID salonId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.id = Objects.requireNonNull(id, "El id de la reserva es obligatorio");
        this.eventoId = Objects.requireNonNull(eventoId, "El evento es obligatorio");
        this.salonId = Objects.requireNonNull(salonId, "El salon es obligatorio");
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio es obligatoria");
        this.fechaFin = Objects.requireNonNull(fechaFin, "La fecha de fin es obligatoria");
        if (!fechaFin.isAfter(fechaInicio)) {
            throw new DomainException("La fecha fin de la reserva debe ser posterior a la fecha inicio");
        }
    }

    public static ReservaSalon nueva(UUID eventoId, UUID salonId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return new ReservaSalon(UUID.randomUUID(), eventoId, salonId, fechaInicio, fechaFin);
    }

    public static ReservaSalon reconstruir(
            UUID id,
            UUID eventoId,
            UUID salonId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    ) {
        return new ReservaSalon(id, eventoId, salonId, fechaInicio, fechaFin);
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public UUID getSalonId() {
        return salonId;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }
}
