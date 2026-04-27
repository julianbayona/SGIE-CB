package com.ejemplo.monolitomodular.usuarios.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;

public class Usuario {

    private final UUID id;
    private final String nombre;
    private final String contrasenaHash;
    private final RolUsuario rol;
    private final boolean activo;

    private Usuario(UUID id, String nombre, String contrasenaHash, RolUsuario rol, boolean activo) {
        this.id = Objects.requireNonNull(id, "El id del usuario es obligatorio");
        this.nombre = validarNombre(nombre);
        this.contrasenaHash = validarContrasenaHash(contrasenaHash);
        this.rol = Objects.requireNonNull(rol, "El rol del usuario es obligatorio");
        this.activo = activo;
    }

    public static Usuario nuevo(String nombre, String contrasenaHash, RolUsuario rol) {
        return new Usuario(UUID.randomUUID(), nombre, contrasenaHash, rol, true);
    }

    public static Usuario reconstruir(
            UUID id,
            String nombre,
            String contrasenaHash,
            RolUsuario rol,
            boolean activo
    ) {
        return new Usuario(id, nombre, contrasenaHash, rol, activo);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DomainException("El nombre del usuario es obligatorio");
        }
        return nombre.trim();
    }

    private static String validarContrasenaHash(String contrasenaHash) {
        if (contrasenaHash == null || contrasenaHash.isBlank()) {
            throw new DomainException("La contrasena del usuario es obligatoria");
        }
        return contrasenaHash.trim();
    }
}
