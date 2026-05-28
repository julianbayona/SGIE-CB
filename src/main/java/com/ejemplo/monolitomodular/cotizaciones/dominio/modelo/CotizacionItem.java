package com.ejemplo.monolitomodular.cotizaciones.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class CotizacionItem {

    private final UUID id;
    private final UUID cotizacionId;
    private final String tipoConcepto;
    private final String conceptoCodigo;
    private final String origenTipo;
    private final UUID origenId;
    private final String descripcion;
    private final String descripcionSnapshot;
    private final BigDecimal precioBase;
    private final BigDecimal precioBaseSnapshot;
    private final BigDecimal precioOverride;
    private final int cantidad;
    private final BigDecimal subtotal;

    private CotizacionItem(
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
            int cantidad
    ) {
        this.id = Objects.requireNonNull(id, "El id del item de cotizacion es obligatorio");
        this.cotizacionId = Objects.requireNonNull(cotizacionId, "La cotizacion es obligatoria");
        if (tipoConcepto == null || tipoConcepto.isBlank()) {
            throw new DomainException("El tipo de concepto es obligatorio");
        }
        this.origenId = Objects.requireNonNull(origenId, "El origen del item de cotizacion es obligatorio");
        String descripcionFinal = descripcionSnapshot == null || descripcionSnapshot.isBlank() ? descripcion : descripcionSnapshot;
        if (descripcionFinal == null || descripcionFinal.isBlank()) {
            throw new DomainException("La descripcion del item es obligatoria");
        }
        BigDecimal precioBaseFinal = precioBaseSnapshot == null ? precioBase : precioBaseSnapshot;
        if (precioBaseFinal == null || precioBaseFinal.signum() < 0) {
            throw new DomainException("El precio base del item no puede ser negativo");
        }
        if (precioOverride != null && precioOverride.signum() < 0) {
            throw new DomainException("El precio override del item no puede ser negativo");
        }
        if (cantidad <= 0) {
            throw new DomainException("La cantidad del item debe ser mayor a cero");
        }
        this.tipoConcepto = tipoConcepto.trim().toUpperCase();
        this.conceptoCodigo = conceptoCodigo == null || conceptoCodigo.isBlank()
                ? this.tipoConcepto
                : conceptoCodigo.trim().toUpperCase();
        this.origenTipo = origenTipo == null || origenTipo.isBlank()
                ? inferirOrigenTipo(this.tipoConcepto)
                : origenTipo.trim().toUpperCase();
        this.descripcion = descripcionFinal.trim();
        this.descripcionSnapshot = this.descripcion;
        this.precioBase = precioBaseFinal;
        this.precioBaseSnapshot = precioBaseFinal;
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
        return new CotizacionItem(UUID.randomUUID(), cotizacionId, tipoConcepto, null, null, origenId, descripcion, null, precioBase, null, precioOverride, cantidad);
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
        return new CotizacionItem(id, cotizacionId, tipoConcepto, null, null, origenId, descripcion, null, precioBase, null, precioOverride, cantidad);
    }

    public static CotizacionItem reconstruirNormalizado(
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
            int cantidad
    ) {
        return new CotizacionItem(
                id,
                cotizacionId,
                tipoConcepto,
                conceptoCodigo,
                origenTipo,
                origenId,
                descripcion,
                descripcionSnapshot,
                precioBase,
                precioBaseSnapshot,
                precioOverride,
                cantidad
        );
    }

    public CotizacionItem actualizarPrecioOverride(BigDecimal precioOverride) {
        return new CotizacionItem(
                id,
                cotizacionId,
                tipoConcepto,
                conceptoCodigo,
                origenTipo,
                origenId,
                descripcion,
                descripcionSnapshot,
                precioBase,
                precioBaseSnapshot,
                precioOverride,
                cantidad
        );
    }

    public CotizacionItem copiarParaCotizacion(UUID nuevaCotizacionId, BigDecimal nuevoPrecioOverride) {
        return new CotizacionItem(
                UUID.randomUUID(),
                nuevaCotizacionId,
                tipoConcepto,
                conceptoCodigo,
                origenTipo,
                origenId,
                descripcion,
                descripcionSnapshot,
                precioBase,
                precioBaseSnapshot,
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

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    private static String inferirOrigenTipo(String tipoConcepto) {
        String tipo = tipoConcepto == null ? "" : tipoConcepto.toUpperCase();
        if (tipo.contains("SALON") || tipo.contains("ALQUILER")) {
            return "SALON";
        }
        if (tipo.contains("MENU") || tipo.contains("PLATO")) {
            return "MENU";
        }
        if (tipo.contains("ADICIONAL")) {
            return "ADICIONAL";
        }
        if (tipo.contains("MONTAJE")) {
            return "MONTAJE";
        }
        return "OTRO";
    }
}
