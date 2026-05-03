package com.ejemplo.monolitomodular.pagos.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "anticipo")
public class AnticipoJpaEntity {

    @Id
    @Column(name = "id_anticipo")
    private UUID id;

    @Column(name = "id_cotizacion", nullable = false)
    private UUID cotizacionId;

    @Column(name = "id_usuario", nullable = false)
    private UUID usuarioId;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "metodo_pago", nullable = false, length = 60)
    private String metodoPago;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    protected AnticipoJpaEntity() {
    }

    public AnticipoJpaEntity(
            UUID id,
            UUID cotizacionId,
            UUID usuarioId,
            BigDecimal valor,
            String metodoPago,
            LocalDate fechaPago,
            String observaciones
    ) {
        this.id = id;
        this.cotizacionId = cotizacionId;
        this.usuarioId = usuarioId;
        this.valor = valor;
        this.metodoPago = metodoPago;
        this.fechaPago = fechaPago;
        this.observaciones = observaciones;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCotizacionId() {
        return cotizacionId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public String getObservaciones() {
        return observaciones;
    }
}
