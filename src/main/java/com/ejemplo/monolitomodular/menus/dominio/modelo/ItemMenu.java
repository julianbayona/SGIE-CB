package com.ejemplo.monolitomodular.menus.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;

public class ItemMenu {

    private final UUID id;
    private final UUID seleccionMenuId;
    private final UUID platoId;
    private final int cantidad;
    private final String excepciones;

    private ItemMenu(UUID id, UUID seleccionMenuId, UUID platoId, int cantidad, String excepciones) {
        this.id = Objects.requireNonNull(id, "El id del item de menu es obligatorio");
        this.seleccionMenuId = Objects.requireNonNull(seleccionMenuId, "La seleccion de menu es obligatoria");
        this.platoId = Objects.requireNonNull(platoId, "El plato es obligatorio");
        if (cantidad <= 0) {
            throw new DomainException("La cantidad del item de menu debe ser mayor a cero");
        }
        this.cantidad = cantidad;
        this.excepciones = excepciones == null || excepciones.isBlank() ? null : excepciones.trim();
    }

    public static ItemMenu nuevo(UUID seleccionMenuId, UUID platoId, int cantidad, String excepciones) {
        return new ItemMenu(UUID.randomUUID(), seleccionMenuId, platoId, cantidad, excepciones);
    }

    public static ItemMenu reconstruir(UUID id, UUID seleccionMenuId, UUID platoId, int cantidad, String excepciones) {
        return new ItemMenu(id, seleccionMenuId, platoId, cantidad, excepciones);
    }

    public UUID getId() {
        return id;
    }

    public UUID getSeleccionMenuId() {
        return seleccionMenuId;
    }

    public UUID getPlatoId() {
        return platoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getExcepciones() {
        return excepciones;
    }
}
