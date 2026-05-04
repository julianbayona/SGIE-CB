package com.ejemplo.monolitomodular.calendario.infraestructura.persistencia;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evento_calendar")
public class EventoCalendarJpaEntity {

    @Id
    @Column(name = "id_evento_calendar")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "origen_tipo", nullable = false, length = 60)
    private OrigenEventoCalendar origenTipo;

    @Column(name = "origen_id", nullable = false)
    private UUID origenId;

    @Column(name = "id_evento", nullable = false)
    private UUID eventoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 60)
    private TipoOperacionCalendar tipo;

    @Column(name = "google_event_id")
    private String googleEventId;

    @Column(name = "fecha_sync")
    private LocalDateTime fechaSync;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 40)
    private EstadoEventoCalendar estado;

    @Column(name = "payload_json", nullable = false, columnDefinition = "text")
    private String payloadJson;

    @Column(name = "intentos", nullable = false)
    private int intentos;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected EventoCalendarJpaEntity() {
    }

    public EventoCalendarJpaEntity(
            UUID id,
            OrigenEventoCalendar origenTipo,
            UUID origenId,
            UUID eventoId,
            TipoOperacionCalendar tipo,
            String googleEventId,
            LocalDateTime fechaSync,
            EstadoEventoCalendar estado,
            String payloadJson,
            int intentos,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.origenTipo = origenTipo;
        this.origenId = origenId;
        this.eventoId = eventoId;
        this.tipo = tipo;
        this.googleEventId = googleEventId;
        this.fechaSync = fechaSync;
        this.estado = estado;
        this.payloadJson = payloadJson;
        this.intentos = intentos;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public OrigenEventoCalendar getOrigenTipo() {
        return origenTipo;
    }

    public UUID getOrigenId() {
        return origenId;
    }

    public UUID getEventoId() {
        return eventoId;
    }

    public TipoOperacionCalendar getTipo() {
        return tipo;
    }

    public String getGoogleEventId() {
        return googleEventId;
    }

    public LocalDateTime getFechaSync() {
        return fechaSync;
    }

    public EstadoEventoCalendar getEstado() {
        return estado;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public int getIntentos() {
        return intentos;
    }
}
