package com.ejemplo.monolitomodular.menus.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Plato {

    private final UUID id;
    private final String nombre;
    private final String descripcion;
    private final BigDecimal precioBase;
    private final boolean activo;

    private Plato(UUID id, String nombre, String descripcion, BigDecimal precioBase, boolean activo) {
        this.id = Objects.requireNonNull(id, "El id del plato es obligatorio");
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre del plato es obligatorio");
        }
        if (precioBase == null || precioBase.signum() < 0) {
            throw new DomainException("El precio base del plato no puede ser negativo");
        }
        this.nombre = nombre.trim();
        this.descripcion = descripcion == null || descripcion.isBlank() ? null : descripcion.trim();
        this.precioBase = precioBase;
        this.activo = activo;
    }

    public static Plato reconstruir(UUID id, String nombre, String descripcion, BigDecimal precioBase, boolean activo) {
        return new Plato(id, nombre, descripcion, precioBase, activo);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public boolean isActivo() {
        return activo;
    }
}
