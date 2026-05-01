package com.ejemplo.monolitomodular.montajes.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "adicional_evento")
public class AdicionalEventoJpaEntity {

    @Id
    @Column(name = "id_adicional_evento")
    private UUID id;

    @Column(name = "id_montaje", nullable = false)
    private UUID montajeId;

    @Column(name = "id_tipo_adicional", nullable = false)
    private UUID tipoAdicionalId;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "precio_override", precision = 12, scale = 2)
    private BigDecimal precioOverride;

    protected AdicionalEventoJpaEntity() {
    }

    public AdicionalEventoJpaEntity(UUID id, UUID montajeId, UUID tipoAdicionalId, int cantidad, BigDecimal precioOverride) {
        this.id = id;
        this.montajeId = montajeId;
        this.tipoAdicionalId = tipoAdicionalId;
        this.cantidad = cantidad;
        this.precioOverride = precioOverride;
    }

    public UUID getId() {
        return id;
    }

    public UUID getMontajeId() {
        return montajeId;
    }

    public UUID getTipoAdicionalId() {
        return tipoAdicionalId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioOverride() {
        return precioOverride;
    }
}
