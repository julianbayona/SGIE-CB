package com.ejemplo.monolitomodular.cotizaciones.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public Cotizacion actualizarItems(Map<UUID, BigDecimal> preciosOverridePorItem) {
        if (estado != EstadoCotizacion.BORRADOR) {
            throw new DomainException("Solo se pueden modificar items de una cotizacion en borrador");
        }
        validarItemsActualizables(preciosOverridePorItem);
        List<CotizacionItem> itemsActualizados = items.stream()
                .map(item -> preciosOverridePorItem.containsKey(item.getId())
                        ? item.actualizarPrecioOverride(preciosOverridePorItem.get(item.getId()))
                        : item)
                .toList();
        return new Cotizacion(id, reservaId, usuarioId, estado, descuento, observaciones, itemsActualizados);
    }

    public Cotizacion crearBorradorActualizado(UUID nuevaCotizacionId, Map<UUID, BigDecimal> preciosOverridePorItem) {
        if (estado != EstadoCotizacion.GENERADA && estado != EstadoCotizacion.ENVIADA) {
            throw new DomainException("Solo una cotizacion generada o enviada puede versionarse por cambios de items");
        }
        validarItemsActualizables(preciosOverridePorItem);
        List<CotizacionItem> itemsCopiados = items.stream()
                .map(item -> item.copiarParaCotizacion(
                        nuevaCotizacionId,
                        preciosOverridePorItem.containsKey(item.getId())
                                ? preciosOverridePorItem.get(item.getId())
                                : item.getPrecioOverride()
                ))
                .toList();
        return crearBorrador(nuevaCotizacionId, reservaId, usuarioId, descuento, observaciones, itemsCopiados);
    }

    private void validarItemsActualizables(Map<UUID, BigDecimal> preciosOverridePorItem) {
        if (preciosOverridePorItem == null || preciosOverridePorItem.isEmpty()) {
            throw new DomainException("Debe enviar al menos un item de cotizacion para actualizar");
        }
        Set<UUID> idsActuales = items.stream()
                .map(CotizacionItem::getId)
                .collect(Collectors.toSet());
        if (!idsActuales.containsAll(preciosOverridePorItem.keySet())) {
            throw new DomainException("Uno o mas items de cotizacion no existen");
        }
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

    public Cotizacion aceptar() {
        if (estado != EstadoCotizacion.ENVIADA) {
            throw new DomainException("Solo una cotizacion enviada puede ser aceptada");
        }
        return new Cotizacion(id, reservaId, usuarioId, EstadoCotizacion.ACEPTADA, descuento, observaciones, items);
    }

    public Cotizacion rechazar() {
        if (estado != EstadoCotizacion.ENVIADA) {
            throw new DomainException("Solo una cotizacion enviada puede ser rechazada");
        }
        return new Cotizacion(id, reservaId, usuarioId, EstadoCotizacion.RECHAZADA, descuento, observaciones, items);
    }

    public Cotizacion desactualizar() {
        if (estado == EstadoCotizacion.RECHAZADA || estado == EstadoCotizacion.DESACTUALIZADA) {
            return this;
        }
        return new Cotizacion(id, reservaId, usuarioId, EstadoCotizacion.DESACTUALIZADA, descuento, observaciones, items);
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
