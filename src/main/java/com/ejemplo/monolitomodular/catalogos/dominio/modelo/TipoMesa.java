package com.ejemplo.monolitomodular.catalogos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;

public class TipoMesa {

    private final UUID id;
    private final String nombre;
    private final boolean activo;

    private TipoMesa(UUID id, String nombre, boolean activo) {
        this.id = Objects.requireNonNull(id, "El id del tipo de mesa es obligatorio");
        this.nombre = validarNombre(nombre);
        this.activo = activo;
    }

    public static TipoMesa nuevo(String nombre) {
        return new TipoMesa(UUID.randomUUID(), nombre, true);
    }

    public static TipoMesa reconstruir(UUID id, String nombre, boolean activo) {
        return new TipoMesa(id, nombre, activo);
    }

    public TipoMesa actualizar(String nombre) {
        return new TipoMesa(id, nombre, activo);
    }

    public TipoMesa desactivar() {
        return new TipoMesa(id, nombre, false);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre del tipo de mesa es obligatorio");
        }
        return nombre.trim();
    }
}
