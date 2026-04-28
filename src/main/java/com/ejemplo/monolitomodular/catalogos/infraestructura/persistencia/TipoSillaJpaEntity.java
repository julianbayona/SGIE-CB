package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tipo_silla")
public class TipoSillaJpaEntity {

    @Id
    @Column(name = "id_tipo_silla")
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected TipoSillaJpaEntity() {
    }

    public TipoSillaJpaEntity(UUID id, String nombre, boolean activo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nombre = nombre;
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

    public boolean isActivo() {
        return activo;
    }
}
