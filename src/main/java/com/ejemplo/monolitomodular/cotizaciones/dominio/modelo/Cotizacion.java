package com.ejemplo.monolitomodular.cotizaciones.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Cotizacion {

    private final UUID id;
    private final UUID reservaId;
    private final UUID usuarioId;
    private final EstadoCotizacion estado;
    private final BigDecimal valorSubtotal;
    private final BigDecimal descuento;
    private final BigDecimal valorTotal;
    private final String observaciones;
    private final List<CotizacionItem> items;

    private Cotizacion(
            UUID id,
            UUID reservaId,
            UUID usuarioId,
            EstadoCotizacion estado,
            BigDecimal descuento,
            String observaciones,
            List<CotizacionItem> items
    ) {
        this.id = Objects.requireNonNull(id, "El id de la cotizacion es obligatorio");
        this.reservaId = Objects.requireNonNull(reservaId, "La reserva de la cotizacion es obligatoria");
        this.usuarioId = Objects.requireNonNull(usuarioId, "El usuario de la cotizacion es obligatorio");
        this.estado = Objects.requireNonNull(estado, "El estado de la cotizacion es obligatorio");
        this.descuento = validarDescuento(descuento);
        this.observaciones = observaciones == null || observaciones.isBlank() ? null : observaciones.trim();
        if (items == null || items.isEmpty()) {
            throw new DomainException("La cotizacion debe tener al menos un item");
        }
        this.items = List.copyOf(items);
        this.valorSubtotal = items.stream()
                .map(CotizacionItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (this.descuento.compareTo(valorSubtotal) > 0) {
            throw new DomainException("El descuento no puede superar el subtotal de la cotizacion");
        }
        this.valorTotal = valorSubtotal.subtract(this.descuento);
    }

    public static Cotizacion crearBorrador(UUID id, UUID reservaId, UUID usuarioId, BigDecimal descuento, String observaciones, List<CotizacionItem> items) {
        return new Cotizacion(id, reservaId, usuarioId, EstadoCotizacion.BORRADOR, descuento, observaciones, items);
    }

    public Cotizacion actualizarItem(UUID itemId, BigDecimal precioOverride) {
        if (estado != EstadoCotizacion.BORRADOR) {
            throw new DomainException("Solo se pueden modificar items de una cotizacion en borrador");
        }
        List<CotizacionItem> itemsActualizados = items.stream()
                .map(item -> item.getId().equals(itemId) ? item.actualizarPrecioOverride(precioOverride) : item)
                .toList();
        boolean existeItem = items.stream().anyMatch(item -> item.getId().equals(itemId));
        if (!existeItem) {
            throw new DomainException("Item de cotizacion no encontrado");
        }
        return new Cotizacion(id, reservaId, usuarioId, estado, descuento, observaciones, itemsActualizados);
    }

    public Cotizacion generarDocumento() {
        if (estado != EstadoCotizacion.BORRADOR) {
            throw new DomainException("Solo una cotizacion en borrador puede pasar a generada");
        }
        return new Cotizacion(id, reservaId, usuarioId, EstadoCotizacion.GENERADA, descuento, observaciones, items);
    }

    public Cotizacion enviar() {
        if (estado != EstadoCotizacion.GENERADA) {
            throw new DomainException("Solo una cotizacion generada puede pasar a enviada");
        }
        return new Cotizacion(id, reservaId, usuarioId, EstadoCotizacion.ENVIADA, descuento, observaciones, items);
    }

    public static Cotizacion reconstruir(
            UUID id,
            UUID reservaId,
            UUID usuarioId,
            EstadoCotizacion estado,
            BigDecimal descuento,
            String observaciones,
            List<CotizacionItem> items
    ) {
        return new Cotizacion(id, reservaId, usuarioId, estado, descuento, observaciones, items);
    }

    private static BigDecimal validarDescuento(BigDecimal descuento) {
        if (descuento == null) {
            return BigDecimal.ZERO;
        }
        if (descuento.signum() < 0) {
            throw new DomainException("El descuento no puede ser negativo");
        }
        return descuento;
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

    public BigDecimal getValorSubtotal() {
        return valorSubtotal;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public List<CotizacionItem> getItems() {
        return items;
    }
}
