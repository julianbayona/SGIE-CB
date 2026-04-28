package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "color")
public class ColorJpaEntity {

    @Id
    @Column(name = "id_color")
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "codigo_hex", nullable = false, length = 7)
    private String codigoHex;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ColorJpaEntity() {
    }

    public ColorJpaEntity(UUID id, String nombre, String codigoHex, boolean activo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nombre = nombre;
        this.codigoHex = codigoHex;
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

    public String getCodigoHex() {
        return codigoHex;
    }

    public boolean isActivo() {
        return activo;
    }
}
