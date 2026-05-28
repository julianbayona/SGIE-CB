package com.ejemplo.monolitomodular.montajes.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "infraestructura_reserva_item")
public class InfraestructuraReservaItemJpaEntity {

    @Id
    @Column(name = "id_infraestructura_item")
    private UUID id;

    @Column(name = "id_infra_reserva", nullable = false)
    private UUID infraestructuraReservaId;

    @Column(name = "id_montaje", nullable = false)
    private UUID montajeId;

    @Column(name = "tipo", nullable = false, length = 60)
    private String tipo;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected InfraestructuraReservaItemJpaEntity() {
    }

    public InfraestructuraReservaItemJpaEntity(
            UUID id,
            UUID infraestructuraReservaId,
            UUID montajeId,
            String tipo,
            int cantidad,
            String observaciones,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.infraestructuraReservaId = infraestructuraReservaId;
        this.montajeId = montajeId;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getInfraestructuraReservaId() {
        return infraestructuraReservaId;
    }

    public UUID getMontajeId() {
        return montajeId;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
