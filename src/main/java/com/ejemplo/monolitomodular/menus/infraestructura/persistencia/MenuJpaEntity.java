package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "menu")
public class MenuJpaEntity {

    @Id
    @Column(name = "id_menu")
    private UUID id;

    @Column(name = "id_reserva", nullable = false)
    private UUID reservaId;

    @Column(name = "notas_generales", length = 500)
    private String notasGenerales;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected MenuJpaEntity() {
    }

    public MenuJpaEntity(UUID id, UUID reservaId, String notasGenerales, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.reservaId = reservaId;
        this.notasGenerales = notasGenerales;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getReservaId() {
        return reservaId;
    }

    public String getNotasGenerales() {
        return notasGenerales;
    }
}
