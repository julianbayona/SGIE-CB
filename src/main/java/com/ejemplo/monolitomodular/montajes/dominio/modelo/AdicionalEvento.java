package com.ejemplo.monolitomodular.montajes.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class AdicionalEvento {

    private final UUID id;
    private final UUID montajeId;
    private final UUID tipoAdicionalId;
    private final int cantidad;
    private final BigDecimal precioOverride;

    private AdicionalEvento(UUID id, UUID montajeId, UUID tipoAdicionalId, int cantidad, BigDecimal precioOverride) {
        this.id = Objects.requireNonNull(id, "El id del adicional es obligatorio");
        this.montajeId = Objects.requireNonNull(montajeId, "El montaje es obligatorio");
        this.tipoAdicionalId = Objects.requireNonNull(tipoAdicionalId, "El tipo adicional es obligatorio");
        if (cantidad <= 0) {
            throw new DomainException("La cantidad del adicional debe ser mayor a cero");
        }
        if (precioOverride != null && precioOverride.signum() < 0) {
            throw new DomainException("El precio override del adicional no puede ser negativo");
        }
        this.cantidad = cantidad;
        this.precioOverride = precioOverride;
    }

    public static AdicionalEvento nuevo(UUID montajeId, UUID tipoAdicionalId, int cantidad, BigDecimal precioOverride) {
        return new AdicionalEvento(UUID.randomUUID(), montajeId, tipoAdicionalId, cantidad, precioOverride);
    }

    public static AdicionalEvento reconstruir(UUID id, UUID montajeId, UUID tipoAdicionalId, int cantidad, BigDecimal precioOverride) {
        return new AdicionalEvento(id, montajeId, tipoAdicionalId, cantidad, precioOverride);
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
