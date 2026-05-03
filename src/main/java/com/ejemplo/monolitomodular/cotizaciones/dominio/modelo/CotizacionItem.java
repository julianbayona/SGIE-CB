package com.ejemplo.monolitomodular.cotizaciones.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class CotizacionItem {

    private final UUID id;
    private final UUID cotizacionId;
    private final String tipoConcepto;
    private final UUID origenId;
    private final String descripcion;
    private final BigDecimal precioBase;
    private final BigDecimal precioOverride;
    private final int cantidad;
    private final BigDecimal subtotal;

    private CotizacionItem(
            UUID id,
            UUID cotizacionId,
            String tipoConcepto,
            UUID origenId,
            String descripcion,
            BigDecimal precioBase,
            BigDecimal precioOverride,
            int cantidad
    ) {
        this.id = Objects.requireNonNull(id, "El id del item de cotizacion es obligatorio");
        this.cotizacionId = Objects.requireNonNull(cotizacionId, "La cotizacion es obligatoria");
        if (tipoConcepto == null || tipoConcepto.isBlank()) {
            throw new DomainException("El tipo de concepto es obligatorio");
        }
        this.origenId = Objects.requireNonNull(origenId, "El origen del item de cotizacion es obligatorio");
        if (descripcion == null || descripcion.isBlank()) {
            throw new DomainException("La descripcion del item es obligatoria");
        }
        if (precioBase == null || precioBase.signum() < 0) {
            throw new DomainException("El precio base del item no puede ser negativo");
        }
        if (precioOverride != null && precioOverride.signum() < 0) {
            throw new DomainException("El precio override del item no puede ser negativo");
        }
        if (cantidad <= 0) {
            throw new DomainException("La cantidad del item debe ser mayor a cero");
        }
        this.tipoConcepto = tipoConcepto.trim().toUpperCase();
        this.descripcion = descripcion.trim();
        this.precioBase = precioBase;
        this.precioOverride = precioOverride;
        this.cantidad = cantidad;
        this.subtotal = precioUnitario().multiply(BigDecimal.valueOf(cantidad));
    }

    public static CotizacionItem nuevo(
            UUID cotizacionId,
            String tipoConcepto,
            UUID origenId,
            String descripcion,
            BigDecimal precioBase,
            BigDecimal precioOverride,
            int cantidad
    ) {
        return new CotizacionItem(UUID.randomUUID(), cotizacionId, tipoConcepto, origenId, descripcion, precioBase, precioOverride, cantidad);
    }

    public static CotizacionItem reconstruir(
            UUID id,
            UUID cotizacionId,
            String tipoConcepto,
            UUID origenId,
            String descripcion,
            BigDecimal precioBase,
            BigDecimal precioOverride,
            int cantidad
    ) {
        return new CotizacionItem(id, cotizacionId, tipoConcepto, origenId, descripcion, precioBase, precioOverride, cantidad);
    }

    public CotizacionItem actualizarPrecioOverride(BigDecimal precioOverride) {
        return new CotizacionItem(id, cotizacionId, tipoConcepto, origenId, descripcion, precioBase, precioOverride, cantidad);
    }

    public CotizacionItem copiarParaCotizacion(UUID nuevaCotizacionId, BigDecimal nuevoPrecioOverride) {
        return CotizacionItem.nuevo(
                nuevaCotizacionId,
                tipoConcepto,
                origenId,
                descripcion,
                precioBase,
                nuevoPrecioOverride,
                cantidad
        );
    }

    public BigDecimal precioUnitario() {
        return precioOverride == null ? precioBase : precioOverride;
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

    public UUID getOrigenId() {
        return origenId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public BigDecimal getPrecioOverride() {
        return precioOverride;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
