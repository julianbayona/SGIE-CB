package com.ejemplo.monolitomodular.montajes.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "montaje")
public class MontajeJpaEntity {

    @Id
    @Column(name = "id_montaje")
    private UUID id;

    @Column(name = "id_reserva", nullable = false)
    private UUID reservaId;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected MontajeJpaEntity() {
    }

    public MontajeJpaEntity(UUID id, UUID reservaId, String observaciones, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.reservaId = reservaId;
        this.observaciones = observaciones;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getReservaId() {
        return reservaId;
    }

    public String getObservaciones() {
        return observaciones;
    }
}
