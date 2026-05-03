package com.ejemplo.monolitomodular.pagos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Anticipo {

    private final UUID id;
    private final UUID cotizacionId;
    private final UUID usuarioId;
    private final BigDecimal valor;
    private final String metodoPago;
    private final LocalDate fechaPago;
    private final String observaciones;

    private Anticipo(
            UUID id,
            UUID cotizacionId,
            UUID usuarioId,
            BigDecimal valor,
            String metodoPago,
            LocalDate fechaPago,
            String observaciones
    ) {
        this.id = Objects.requireNonNull(id, "El id del anticipo es obligatorio");
        this.cotizacionId = Objects.requireNonNull(cotizacionId, "La cotizacion del anticipo es obligatoria");
        this.usuarioId = Objects.requireNonNull(usuarioId, "El usuario del anticipo es obligatorio");
        this.valor = validarValor(valor);
        this.metodoPago = validarMetodoPago(metodoPago);
        this.fechaPago = Objects.requireNonNull(fechaPago, "La fecha de pago es obligatoria");
        this.observaciones = observaciones == null || observaciones.isBlank() ? null : observaciones.trim();
    }

    public static Anticipo nuevo(
            UUID cotizacionId,
            UUID usuarioId,
            BigDecimal valor,
            String metodoPago,
            LocalDate fechaPago,
            String observaciones
    ) {
        return new Anticipo(UUID.randomUUID(), cotizacionId, usuarioId, valor, metodoPago, fechaPago, observaciones);
    }

    public static Anticipo reconstruir(
            UUID id,
            UUID cotizacionId,
            UUID usuarioId,
            BigDecimal valor,
            String metodoPago,
            LocalDate fechaPago,
            String observaciones
    ) {
        return new Anticipo(id, cotizacionId, usuarioId, valor, metodoPago, fechaPago, observaciones);
    }

    private static BigDecimal validarValor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new DomainException("El valor del anticipo debe ser mayor a cero");
        }
        return valor;
    }

    private static String validarMetodoPago(String metodoPago) {
        if (metodoPago == null || metodoPago.isBlank()) {
            throw new DomainException("El metodo de pago es obligatorio");
        }
        return metodoPago.trim();
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
