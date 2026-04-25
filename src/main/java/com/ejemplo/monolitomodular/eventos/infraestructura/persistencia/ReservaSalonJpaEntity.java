package com.ejemplo.monolitomodular.eventos.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reserva_salon")
public class ReservaSalonJpaEntity {

    @Id
    private UUID id;

    @Column(name = "evento_id", nullable = false)
    private UUID eventoId;

    @Column(name = "salon_id", nullable = false)
    private UUID salonId;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ReservaSalonJpaEntity() {
    }

    public ReservaSalonJpaEntity(
            UUID id,
            UUID eventoId,
            UUID salonId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.eventoId = eventoId;
        this.salonId = salonId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.createdAt = createdAt;
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
