package com.ejemplo.monolitomodular.menus.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Menu {

    private final UUID id;
    private final UUID reservaId;
    private final String notasGenerales;
    private final List<SeleccionMenu> selecciones;

    private Menu(UUID id, UUID reservaId, String notasGenerales, List<SeleccionMenu> selecciones) {
        this.id = Objects.requireNonNull(id, "El id del menu es obligatorio");
        this.reservaId = Objects.requireNonNull(reservaId, "La reserva del menu es obligatoria");
        this.notasGenerales = notasGenerales == null || notasGenerales.isBlank() ? null : notasGenerales.trim();
        if (selecciones == null || selecciones.isEmpty()) {
            throw new DomainException("El menu debe tener al menos una seleccion");
        }
        this.selecciones = List.copyOf(selecciones);
    }

    public static Menu configurar(UUID id, UUID reservaId, String notasGenerales, List<SeleccionMenu> selecciones) {
        return new Menu(id, reservaId, notasGenerales, selecciones);
    }

    public static Menu reconstruir(UUID id, UUID reservaId, String notasGenerales, List<SeleccionMenu> selecciones) {
        return new Menu(id, reservaId, notasGenerales, selecciones);
    }

    public UUID getId() {
        return id;
    }

    public UUID getReservaId() {
        return reservaId;
    }

    public String getNotasGenerales() {
        return notasGenerales;
    }

    public List<SeleccionMenu> getSelecciones() {
        return selecciones;
    }
}
