package com.ejemplo.monolitomodular.clientes.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Cliente {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final UUID id;
    private final String cedula;
    private final String nombre;
    private final String telefono;
    private final String correo;
    private final TipoCliente tipoCliente;
    private final boolean activo;

    private Cliente(
            UUID id,
            String cedula,
            String nombre,
            String telefono,
            String correo,
            TipoCliente tipoCliente,
            boolean activo
    ) {
        this.id = Objects.requireNonNull(id, "El id es obligatorio");
        this.cedula = validarCedula(cedula);
        this.nombre = validarNombre(nombre);
        this.telefono = validarTelefono(telefono);
        this.correo = validarCorreo(correo);
        this.tipoCliente = Objects.requireNonNull(tipoCliente, "El tipo de cliente es obligatorio");
        this.activo = activo;
    }

    public static Cliente nuevo(
            String cedula,
            String nombre,
            String telefono,
            String correo,
            TipoCliente tipoCliente
    ) {
        return new Cliente(UUID.randomUUID(), cedula, nombre, telefono, correo, tipoCliente, true);
    }

    public static Cliente reconstruir(
            UUID id,
            String cedula,
            String nombre,
            String telefono,
            String correo,
            TipoCliente tipoCliente,
            boolean activo
    ) {
        return new Cliente(id, cedula, nombre, telefono, correo, tipoCliente, activo);
    }

    public UUID getId() {
        return id;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public boolean isActivo() {
        return activo;
    }

    private static String validarCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            throw new DomainException("La cedula del cliente es obligatoria");
        }
        return cedula.trim();
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre del cliente es obligatorio");
        }
        return nombre.trim();
    }

    private static String validarTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new DomainException("El telefono del cliente es obligatorio");
        }
        return telefono.trim();
    }

    private static String validarCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            throw new DomainException("El correo del cliente es obligatorio");
        }
        String correoNormalizado = correo.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(correoNormalizado).matches()) {
            throw new DomainException("El correo del cliente no es valido");
        }
        return correoNormalizado;
    }
}
