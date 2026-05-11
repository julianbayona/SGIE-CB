package com.ejemplo.monolitomodular.cotizaciones.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cotizacion_reserva")
@IdClass(CotizacionReservaJpaId.class)
public class CotizacionReservaJpaEntity {

    @Id
    @Column(name = "id_cotizacion")
    private UUID cotizacionId;

    @Id
    @Column(name = "id_reserva")
    private UUID reservaId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected CotizacionReservaJpaEntity() {
    }

    public CotizacionReservaJpaEntity(UUID cotizacionId, UUID reservaId, LocalDateTime createdAt) {
        this.cotizacionId = cotizacionId;
        this.reservaId = reservaId;
        this.createdAt = createdAt;
    }
}
