package com.ejemplo.monolitomodular.catalogos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;

public class Mantel {

    private final UUID id;
    private final String nombre;
    private final UUID colorId;
    private final boolean activo;

    private Mantel(UUID id, String nombre, UUID colorId, boolean activo) {
        this.id = Objects.requireNonNull(id, "El id del mantel es obligatorio");
        this.nombre = validarNombre(nombre);
        this.colorId = Objects.requireNonNull(colorId, "El color del mantel es obligatorio");
        this.activo = activo;
    }

    public static Mantel nuevo(String nombre, UUID colorId) {
        return new Mantel(UUID.randomUUID(), nombre, colorId, true);
    }

    public static Mantel reconstruir(UUID id, String nombre, UUID colorId, boolean activo) {
        return new Mantel(id, nombre, colorId, activo);
    }

    public Mantel actualizar(String nombre, UUID colorId) {
        return new Mantel(id, nombre, colorId, activo);
    }

    public Mantel desactivar() {
        return new Mantel(id, nombre, colorId, false);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public UUID getColorId() {
        return colorId;
    }

    public boolean isActivo() {
        return activo;
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre del mantel es obligatorio");
        }
        return nombre.trim();
    }
}
