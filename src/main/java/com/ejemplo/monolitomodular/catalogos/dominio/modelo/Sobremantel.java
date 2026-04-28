package com.ejemplo.monolitomodular.catalogos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;

public class Sobremantel {

    private final UUID id;
    private final String nombre;
    private final UUID colorId;
    private final boolean activo;

    private Sobremantel(UUID id, String nombre, UUID colorId, boolean activo) {
        this.id = Objects.requireNonNull(id, "El id del sobremantel es obligatorio");
        this.nombre = validarNombre(nombre);
        this.colorId = Objects.requireNonNull(colorId, "El color del sobremantel es obligatorio");
        this.activo = activo;
    }

    public static Sobremantel nuevo(String nombre, UUID colorId) {
        return new Sobremantel(UUID.randomUUID(), nombre, colorId, true);
    }

    public static Sobremantel reconstruir(UUID id, String nombre, UUID colorId, boolean activo) {
        return new Sobremantel(id, nombre, colorId, activo);
    }

    public Sobremantel actualizar(String nombre, UUID colorId) {
        return new Sobremantel(id, nombre, colorId, activo);
    }

    public Sobremantel desactivar() {
        return new Sobremantel(id, nombre, colorId, false);
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
            throw new DomainException("El nombre del sobremantel es obligatorio");
        }
        return nombre.trim();
    }
}
