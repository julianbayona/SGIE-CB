package com.ejemplo.monolitomodular.auth.infraestructura.seguridad;

import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;

import java.time.Instant;
import java.util.UUID;

public record UsuarioAutenticado(
        UUID id,
        String nombre,
        RolUsuario rol,
        Instant expiraEn
) {
}
