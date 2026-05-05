package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "plato")
public class PlatoJpaEntity {

    @Id
    @Column(name = "id_plato")
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "precio_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioBase;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected PlatoJpaEntity() {
    }

    public PlatoJpaEntity(
            UUID id,
            String nombre,
            String descripcion,
            BigDecimal precioBase,
            boolean activo,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
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

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
