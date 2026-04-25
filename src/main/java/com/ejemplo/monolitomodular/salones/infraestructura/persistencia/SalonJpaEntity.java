package com.ejemplo.monolitomodular.salones.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "salon")
public class SalonJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 120)
    private String nombre;

    @Column(nullable = false)
    private int capacidad;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected SalonJpaEntity() {
    }

    public SalonJpaEntity(
            UUID id,
            String nombre,
            int capacidad,
            String descripcion,
            boolean activo,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.descripcion = descripcion;
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

    public int getCapacidad() {
        return capacidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActivo() {
        return activo;
    }
}
