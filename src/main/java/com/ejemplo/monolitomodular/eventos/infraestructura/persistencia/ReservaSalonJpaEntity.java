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
    @Column(name = "id_reserva")
    private UUID id;

    @Column(name = "reserva_raiz_id", nullable = false)
    private UUID reservaRaizId;

    @Column(name = "id_evento", nullable = false)
    private UUID eventoId;

    @Column(name = "id_salon", nullable = false)
    private UUID salonId;

    @Column(name = "num_invitados", nullable = false)
    private int numInvitados;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    @Column(nullable = false)
    private int version;

    @Column(nullable = false)
    private boolean vigente;

    @Column(name = "creado_por", nullable = false)
    private UUID creadoPor;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ReservaSalonJpaEntity() {
    }

    public ReservaSalonJpaEntity(
            UUID id,
            UUID reservaRaizId,
            UUID eventoId,
            UUID salonId,
            int numInvitados,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            int version,
            boolean vigente,
            UUID creadoPor,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.reservaRaizId = reservaRaizId;
        this.eventoId = eventoId;
        this.salonId = salonId;
        this.numInvitados = numInvitados;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.version = version;
        this.vigente = vigente;
        this.creadoPor = creadoPor;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public UUID getReservaRaizId() {
        return reservaRaizId;
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

    public int getVersion() {
        return version;
    }

    public boolean isVigente() {
        return vigente;
    }

    public UUID getCreadoPor() {
        return creadoPor;
    }
}
