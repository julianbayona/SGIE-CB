package com.ejemplo.monolitomodular.catalogos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;

public class TipoSilla {

    private final UUID id;
    private final String nombre;
    private final boolean activo;

    private TipoSilla(UUID id, String nombre, boolean activo) {
        this.id = Objects.requireNonNull(id, "El id del tipo de silla es obligatorio");
        this.nombre = validarNombre(nombre);
        this.activo = activo;
    }

    public static TipoSilla nuevo(String nombre) {
        return new TipoSilla(UUID.randomUUID(), nombre, true);
    }

    public static TipoSilla reconstruir(UUID id, String nombre, boolean activo) {
        return new TipoSilla(id, nombre, activo);
    }

    public TipoSilla actualizar(String nombre) {
        return new TipoSilla(id, nombre, activo);
    }

    public TipoSilla desactivar() {
        return new TipoSilla(id, nombre, false);
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
            throw new DomainException("El nombre del tipo de silla es obligatorio");
        }
        return nombre.trim();
    }
}
