package com.ejemplo.monolitomodular.usuarios.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Usuario {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final UUID id;
    private final String nombre;
    private final String email;
    private final String passwordHash;
    private final RolUsuario rol;
    private final boolean activo;

    private Usuario(UUID id, String nombre, String email, String passwordHash, RolUsuario rol, boolean activo) {
        this.id = Objects.requireNonNull(id, "El id del usuario es obligatorio");
        this.nombre = validarNombre(nombre);
        this.email = validarEmail(email);
        this.passwordHash = validarPasswordHash(passwordHash);
        this.rol = Objects.requireNonNull(rol, "El rol del usuario es obligatorio");
        this.activo = activo;
    }

    public static Usuario nuevo(String nombre, String email, String passwordHash, RolUsuario rol) {
        return new Usuario(UUID.randomUUID(), nombre, email, passwordHash, rol, true);
    }

    public static Usuario reconstruir(
            UUID id,
            String nombre,
            String email,
            String passwordHash,
            RolUsuario rol,
            boolean activo
    ) {
        return new Usuario(id, nombre, email, passwordHash, rol, activo);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
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

    private static String validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new DomainException("El email del usuario es obligatorio");
        }
        String emailNormalizado = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(emailNormalizado).matches()) {
            throw new DomainException("El email del usuario no es valido");
        }
        return emailNormalizado;
    }

    private static String validarPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new DomainException("La contrasena del usuario es obligatoria");
        }
        return passwordHash.trim();
    }
}
