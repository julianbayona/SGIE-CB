package com.ejemplo.monolitomodular.pruebasplato.infraestructura.persistencia;

import com.ejemplo.monolitomodular.pruebasplato.dominio.modelo.EstadoPruebaPlato;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prueba_plato")
public class PruebaPlatoJpaEntity {

    @Id
    @Column(name = "id_prueba_plato")
    private UUID id;

    @Column(name = "id_evento", nullable = false)
    private UUID eventoId;

    @Column(name = "fecha_realizacion", nullable = false)
    private LocalDateTime fechaRealizacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 40)
    private EstadoPruebaPlato estado;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected PruebaPlatoJpaEntity() {
    }

    public PruebaPlatoJpaEntity(
            UUID id,
            UUID eventoId,
            LocalDateTime fechaRealizacion,
            EstadoPruebaPlato estado,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.eventoId = eventoId;
        this.fechaRealizacion = fechaRealizacion;
        this.estado = estado;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public LocalDateTime getFechaRealizacion() {
        return fechaRealizacion;
    }

    public EstadoPruebaPlato getEstado() {
        return estado;
    }
}
