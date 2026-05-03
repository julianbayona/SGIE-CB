package com.ejemplo.monolitomodular.menus.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SeleccionMenu {

    private final UUID id;
    private final UUID menuId;
    private final UUID tipoMomentoId;
    private final List<ItemMenu> items;

    private SeleccionMenu(UUID id, UUID menuId, UUID tipoMomentoId, List<ItemMenu> items) {
        this.id = Objects.requireNonNull(id, "El id de la seleccion de menu es obligatorio");
        this.menuId = Objects.requireNonNull(menuId, "El menu es obligatorio");
        this.tipoMomentoId = Objects.requireNonNull(tipoMomentoId, "El momento de menu es obligatorio");
        if (items == null || items.isEmpty()) {
            throw new DomainException("La seleccion de menu debe tener al menos un item");
        }
        this.items = List.copyOf(items);
    }

    public static SeleccionMenu nueva(UUID id, UUID menuId, UUID tipoMomentoId, List<ItemMenu> items) {
        return new SeleccionMenu(id, menuId, tipoMomentoId, items);
    }

    public static SeleccionMenu reconstruir(UUID id, UUID menuId, UUID tipoMomentoId, List<ItemMenu> items) {
        return new SeleccionMenu(id, menuId, tipoMomentoId, items);
    }

    public UUID getId() {
        return id;
    }

    public UUID getMenuId() {
        return menuId;
    }

    public UUID getTipoMomentoId() {
        return tipoMomentoId;
    }

    public List<ItemMenu> getItems() {
        return items;
    }
}
