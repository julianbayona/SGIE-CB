package com.ejemplo.monolitomodular.cotizaciones.infraestructura.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cotizacion_item")
public class CotizacionItemJpaEntity {

    @Id
    @Column(name = "id_cotizacion_item")
    private UUID id;

    @Column(name = "id_cotizacion", nullable = false)
    private UUID cotizacionId;

    @Column(name = "tipo_concepto", nullable = false, length = 60)
    private String tipoConcepto;

    @Column(name = "concepto_codigo", length = 60)
    private String conceptoCodigo;

    @Column(name = "origen_tipo", length = 40)
    private String origenTipo;

    @Column(name = "origen_id", nullable = false)
    private UUID origenId;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    @Column(name = "descripcion_snapshot", length = 500)
    private String descripcionSnapshot;

    @Column(name = "precio_base", nullable = false)
    private BigDecimal precioBase;

    @Column(name = "precio_base_snapshot")
    private BigDecimal precioBaseSnapshot;

    @Column(name = "precio_override")
    private BigDecimal precioOverride;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    protected CotizacionItemJpaEntity() {
    }

    public CotizacionItemJpaEntity(
            UUID id,
            UUID cotizacionId,
            String tipoConcepto,
            String conceptoCodigo,
            String origenTipo,
            UUID origenId,
            String descripcion,
            String descripcionSnapshot,
            BigDecimal precioBase,
            BigDecimal precioBaseSnapshot,
            BigDecimal precioOverride,
            int cantidad,
            BigDecimal subtotal
    ) {
        this.id = id;
        this.cotizacionId = cotizacionId;
        this.tipoConcepto = tipoConcepto;
        this.conceptoCodigo = conceptoCodigo;
        this.origenTipo = origenTipo;
        this.origenId = origenId;
        this.descripcion = descripcion;
        this.descripcionSnapshot = descripcionSnapshot;
        this.precioBase = precioBase;
        this.precioBaseSnapshot = precioBaseSnapshot;
        this.precioOverride = precioOverride;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCotizacionId() {
        return cotizacionId;
    }

    public String getTipoConcepto() {
        return tipoConcepto;
    }

    public String getConceptoCodigo() {
        return conceptoCodigo;
    }

    public String getOrigenTipo() {
        return origenTipo;
    }

    public UUID getOrigenId() {
        return origenId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDescripcionSnapshot() {
        return descripcionSnapshot;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public BigDecimal getPrecioBaseSnapshot() {
        return precioBaseSnapshot;
    }

    public BigDecimal getPrecioOverride() {
        return precioOverride;
    }

    public int getCantidad() {
        return cantidad;
    }
}
