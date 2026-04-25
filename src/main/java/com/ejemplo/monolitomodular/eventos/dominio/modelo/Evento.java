package com.ejemplo.monolitomodular.eventos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

public class Evento {

    private final UUID id;
    private final UUID clienteId;
    private final String tipoEvento;
    private final String tipoComida;
    private final LocalDate fechaEvento;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private final int numeroPersonas;
    private final EstadoEvento estado;
    private final String observaciones;

    private Evento(
            UUID id,
            UUID clienteId,
            String tipoEvento,
            String tipoComida,
            LocalDate fechaEvento,
            LocalTime horaInicio,
            LocalTime horaFin,
            int numeroPersonas,
            EstadoEvento estado,
            String observaciones
    ) {
        this.id = Objects.requireNonNull(id, "El id del evento es obligatorio");
        this.clienteId = Objects.requireNonNull(clienteId, "El cliente del evento es obligatorio");
        this.tipoEvento = validarTexto(tipoEvento, "El tipo de evento es obligatorio");
        this.tipoComida = validarTexto(tipoComida, "El tipo de comida es obligatorio");
        this.fechaEvento = Objects.requireNonNull(fechaEvento, "La fecha del evento es obligatoria");
        this.horaInicio = Objects.requireNonNull(horaInicio, "La hora de inicio es obligatoria");
        this.horaFin = Objects.requireNonNull(horaFin, "La hora de fin es obligatoria");
        if (!horaFin.isAfter(horaInicio)) {
            throw new DomainException("La hora de fin debe ser posterior a la hora de inicio");
        }
        if (numeroPersonas <= 0) {
            throw new DomainException("El numero de personas debe ser mayor a cero");
        }
        this.numeroPersonas = numeroPersonas;
        this.estado = Objects.requireNonNull(estado, "El estado del evento es obligatorio");
        this.observaciones = observaciones == null ? "" : observaciones.trim();
    }

    public static Evento nuevo(
            UUID clienteId,
            String tipoEvento,
            String tipoComida,
            LocalDate fechaEvento,
            LocalTime horaInicio,
            int duracionHoras,
            int numeroPersonas,
            String observaciones
    ) {
        if (duracionHoras < 2 || duracionHoras > 7) {
            throw new DomainException("La duracion del evento debe estar entre 2 y 7 horas");
        }
        return new Evento(
                UUID.randomUUID(),
                clienteId,
                tipoEvento,
                tipoComida,
                fechaEvento,
                horaInicio,
                horaInicio.plusHours(duracionHoras),
                numeroPersonas,
                EstadoEvento.PENDIENTE,
                observaciones
        );
    }

    public static Evento reconstruir(
            UUID id,
            UUID clienteId,
            String tipoEvento,
            String tipoComida,
            LocalDate fechaEvento,
            LocalTime horaInicio,
            LocalTime horaFin,
            int numeroPersonas,
            EstadoEvento estado,
            String observaciones
    ) {
        return new Evento(id, clienteId, tipoEvento, tipoComida, fechaEvento, horaInicio, horaFin, numeroPersonas, estado, observaciones);
    }

    public UUID getId() {
        return id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public String getTipoComida() {
        return tipoComida;
    }

    public LocalDate getFechaEvento() {
        return fechaEvento;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public int getNumeroPersonas() {
        return numeroPersonas;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    private static String validarTexto(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new DomainException(mensaje);
        }
        return valor.trim();
    }
}
