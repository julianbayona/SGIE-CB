package com.ejemplo.monolitomodular.pruebasplato.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class PruebaPlato {

    private final UUID id;
    private final UUID eventoId;
    private final LocalDateTime fechaRealizacion;
    private final EstadoPruebaPlato estado;

    private PruebaPlato(
            UUID id,
            UUID eventoId,
            LocalDateTime fechaRealizacion,
            EstadoPruebaPlato estado
    ) {
        this.id = Objects.requireNonNull(id, "El id de la prueba de plato es obligatorio");
        this.eventoId = Objects.requireNonNull(eventoId, "El evento de la prueba de plato es obligatorio");
        this.fechaRealizacion = Objects.requireNonNull(fechaRealizacion, "La fecha de realizacion es obligatoria");
        this.estado = Objects.requireNonNull(estado, "El estado de la prueba de plato es obligatorio");
        if (fechaRealizacion.isBefore(LocalDateTime.now())) {
            throw new DomainException("La prueba de plato no puede programarse en una fecha pasada");
        }
    }

    public static PruebaPlato programar(UUID eventoId, LocalDateTime fechaRealizacion) {
        return new PruebaPlato(UUID.randomUUID(), eventoId, fechaRealizacion, EstadoPruebaPlato.PROGRAMADA);
    }

    public static PruebaPlato reconstruir(
            UUID id,
            UUID eventoId,
            LocalDateTime fechaRealizacion,
            EstadoPruebaPlato estado
    ) {
        return new PruebaPlato(id, eventoId, fechaRealizacion, estado);
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public LocalDateTime getFechaRealizacion() {
        return fechaRealizacion;
    }

    public EstadoPruebaPlato getEstado() {
        return estado;
    }
}
