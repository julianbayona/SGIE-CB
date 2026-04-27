package com.ejemplo.monolitomodular.catalogos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;

public class TipoComida {

    private final UUID id;
    private final String nombre;
    private final String descripcion;
    private final boolean activo;

    private TipoComida(UUID id, String nombre, String descripcion, boolean activo) {
        this.id = Objects.requireNonNull(id, "El id del tipo de comida es obligatorio");
        this.nombre = validarNombre(nombre);
        this.descripcion = descripcion == null ? "" : descripcion.trim();
        this.activo = activo;
    }

    public static TipoComida nuevo(String nombre, String descripcion) {
        return new TipoComida(UUID.randomUUID(), nombre, descripcion, true);
    }

    public static TipoComida reconstruir(UUID id, String nombre, String descripcion, boolean activo) {
        return new TipoComida(id, nombre, descripcion, activo);
    }

    public TipoComida actualizar(String nombre, String descripcion) {
        return new TipoComida(id, nombre, descripcion, activo);
    }

    public TipoComida desactivar() {
        return new TipoComida(id, nombre, descripcion, false);
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

    public boolean isActivo() {
        return activo;
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre del tipo de comida es obligatorio");
        }
        return nombre.trim();
    }
}
