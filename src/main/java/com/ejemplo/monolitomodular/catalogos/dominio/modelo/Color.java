package com.ejemplo.monolitomodular.catalogos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Color {

    private static final Pattern HEX_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");

    private final UUID id;
    private final String nombre;
    private final String codigoHex;
    private final boolean activo;

    private Color(UUID id, String nombre, String codigoHex, boolean activo) {
        this.id = Objects.requireNonNull(id, "El id del color es obligatorio");
        this.nombre = validarNombre(nombre);
        this.codigoHex = validarCodigoHex(codigoHex);
        this.activo = activo;
    }

    public static Color nuevo(String nombre, String codigoHex) {
        return new Color(UUID.randomUUID(), nombre, codigoHex, true);
    }

    public static Color reconstruir(UUID id, String nombre, String codigoHex, boolean activo) {
        return new Color(id, nombre, codigoHex, activo);
    }

    public Color actualizar(String nombre, String codigoHex) {
        return new Color(id, nombre, codigoHex, activo);
    }

    public Color desactivar() {
        return new Color(id, nombre, codigoHex, false);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCodigoHex() {
        return codigoHex;
    }

    public boolean isActivo() {
        return activo;
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre del color es obligatorio");
        }
        return nombre.trim();
    }

    private static String validarCodigoHex(String codigoHex) {
        if (codigoHex == null || !HEX_PATTERN.matcher(codigoHex.trim()).matches()) {
            throw new DomainException("El codigo hexadecimal del color no es valido");
        }
        return codigoHex.trim().toUpperCase();
    }
}
