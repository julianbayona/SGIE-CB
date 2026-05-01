package com.ejemplo.monolitomodular.menus.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;

public class TipoMomentoMenu {

    private final UUID id;
    private final String nombre;
    private final boolean activo;

    private TipoMomentoMenu(UUID id, String nombre, boolean activo) {
        this.id = Objects.requireNonNull(id, "El id del momento de menu es obligatorio");
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre del momento de menu es obligatorio");
        }
        this.nombre = nombre.trim();
        this.activo = activo;
    }

    public static TipoMomentoMenu reconstruir(UUID id, String nombre, boolean activo) {
        return new TipoMomentoMenu(id, nombre, activo);
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
}
