package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mantel")
public class MantelJpaEntity {

    @Id
    @Column(name = "id_mantel")
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "id_color", nullable = false)
    private UUID colorId;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected MantelJpaEntity() {
    }

    public MantelJpaEntity(UUID id, String nombre, UUID colorId, boolean activo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nombre = nombre;
        this.colorId = colorId;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public UUID getColorId() {
        return colorId;
    }

    public boolean isActivo() {
        return activo;
    }
}
