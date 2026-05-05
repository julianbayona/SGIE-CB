package com.ejemplo.monolitomodular.auth.aplicacion.dto;

import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;

import java.time.Instant;
import java.util.UUID;

public record UsuarioAutenticadoView(
        UUID usuarioId,
        String nombre,
        RolUsuario rol,
        Instant tokenExpiraEn
) {
}
