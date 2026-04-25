package com.ejemplo.monolitomodular.salones.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;

public class Salon {

    private final UUID id;
    private final String nombre;
    private final int capacidad;
    private final String descripcion;
    private final boolean activo;

    private Salon(UUID id, String nombre, int capacidad, String descripcion, boolean activo) {
        this.id = Objects.requireNonNull(id, "El id del salon es obligatorio");
        this.nombre = validarNombre(nombre);
        this.capacidad = validarCapacidad(capacidad);
        this.descripcion = descripcion == null ? "" : descripcion.trim();
        this.activo = activo;
    }

    public static Salon nuevo(String nombre, int capacidad, String descripcion) {
        return new Salon(UUID.randomUUID(), nombre, capacidad, descripcion, true);
    }

    public static Salon reconstruir(UUID id, String nombre, int capacidad, String descripcion, boolean activo) {
        return new Salon(id, nombre, capacidad, descripcion, activo);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre del salon es obligatorio");
        }
        return nombre.trim();
    }

    private static int validarCapacidad(int capacidad) {
        if (capacidad <= 0) {
            throw new DomainException("La capacidad del salon debe ser mayor a cero");
        }
        return capacidad;
    }
}
