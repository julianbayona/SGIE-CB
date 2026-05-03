package com.ejemplo.monolitomodular.cotizaciones.infraestructura.persistencia;

import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cotizacion")
public class CotizacionJpaEntity {

    @Id
    @Column(name = "id_cotizacion")
    private UUID id;

    @Column(name = "id_reserva", nullable = false)
    private UUID reservaId;

    @Column(name = "id_usuario", nullable = false)
    private UUID usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 40)
    private EstadoCotizacion estado;

    @Column(name = "vigente", nullable = false)
    private boolean vigente;

    @Column(name = "valor_subtotal", nullable = false)
    private BigDecimal valorSubtotal;

    @Column(name = "descuento", nullable = false)
    private BigDecimal descuento;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected CotizacionJpaEntity() {
    }

    public CotizacionJpaEntity(
            UUID id,
            UUID reservaId,
            UUID usuarioId,
            EstadoCotizacion estado,
            boolean vigente,
            BigDecimal valorSubtotal,
            BigDecimal descuento,
            BigDecimal valorTotal,
            String observaciones,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.reservaId = reservaId;
        this.usuarioId = usuarioId;
        this.estado = estado;
        this.vigente = vigente;
        this.valorSubtotal = valorSubtotal;
        this.descuento = descuento;
        this.valorTotal = valorTotal;
        this.observaciones = observaciones;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getReservaId() {
        return reservaId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public EstadoCotizacion getEstado() {
        return estado;
    }

    public boolean isVigente() {
        return vigente;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public String getObservaciones() {
        return observaciones;
    }
}
