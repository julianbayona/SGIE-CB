package com.ejemplo.monolitomodular.catalogos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;

public class TipoEvento {

    private final UUID id;
    private final String nombre;
    private final String descripcion;
    private final boolean activo;

    private TipoEvento(UUID id, String nombre, String descripcion, boolean activo) {
        this.id = Objects.requireNonNull(id, "El id del tipo de evento es obligatorio");
        this.nombre = validarNombre(nombre);
        this.descripcion = descripcion == null ? "" : descripcion.trim();
        this.activo = activo;
    }

    public static TipoEvento nuevo(String nombre, String descripcion) {
        return new TipoEvento(UUID.randomUUID(), nombre, descripcion, true);
    }

    public static TipoEvento reconstruir(UUID id, String nombre, String descripcion, boolean activo) {
        return new TipoEvento(id, nombre, descripcion, activo);
    }

    public TipoEvento actualizar(String nombre, String descripcion) {
        return new TipoEvento(id, nombre, descripcion, activo);
    }

    public TipoEvento desactivar() {
        return new TipoEvento(id, nombre, descripcion, false);
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
            throw new DomainException("El nombre del tipo de evento es obligatorio");
        }
        return nombre.trim();
    }
}
