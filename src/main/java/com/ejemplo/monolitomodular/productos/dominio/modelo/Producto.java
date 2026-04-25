package com.ejemplo.monolitomodular.productos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Producto {

    private final UUID id;
    private final String nombre;
    private final BigDecimal precio;

    private Producto(UUID id, String nombre, BigDecimal precio) {
        this.id = Objects.requireNonNull(id, "El id es obligatorio");
        this.nombre = validarNombre(nombre);
        this.precio = validarPrecio(precio);
    }

    public static Producto nuevo(String nombre, BigDecimal precio) {
        return new Producto(UUID.randomUUID(), nombre, precio);
    }

    public static Producto reconstruir(UUID id, String nombre, BigDecimal precio) {
        return new Producto(id, nombre, precio);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre del producto es obligatorio");
        }
        return nombre.trim();
    }

    private static BigDecimal validarPrecio(BigDecimal precio) {
        if (precio == null) {
            throw new DomainException("El precio del producto es obligatorio");
        }
        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("El precio del producto debe ser mayor que cero");
        }
        return precio;
    }
}
