package com.ejemplo.monolitomodular.eventos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "evento")
public class EventoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(name = "tipo_evento", nullable = false, length = 100)
    private String tipoEvento;

    @Column(name = "tipo_comida", nullable = false, length = 100)
    private String tipoComida;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDate fechaEvento;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "numero_personas", nullable = false)
    private int numeroPersonas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EstadoEvento estado;

    @Column(columnDefinition = "text")
    private String observaciones;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected EventoJpaEntity() {
    }

    public EventoJpaEntity(
            UUID id,
            UUID clienteId,
            String tipoEvento,
            String tipoComida,
            LocalDate fechaEvento,
            LocalTime horaInicio,
            LocalTime horaFin,
            int numeroPersonas,
            EstadoEvento estado,
            String observaciones,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.clienteId = clienteId;
        this.tipoEvento = tipoEvento;
        this.tipoComida = tipoComida;
        this.fechaEvento = fechaEvento;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.numeroPersonas = numeroPersonas;
        this.estado = estado;
        this.observaciones = observaciones;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public String getTipoComida() {
        return tipoComida;
    }

    public LocalDate getFechaEvento() {
        return fechaEvento;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public int getNumeroPersonas() {
        return numeroPersonas;
    }

    public EstadoEvento getEstado() {
        return estado;
    }

    public String getObservaciones() {
        return observaciones;
    }
}
