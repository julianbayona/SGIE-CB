package com.ejemplo.monolitomodular.usuarios.aplicacion.dto;

import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;

import java.util.UUID;

public record UsuarioView(
        UUID id,
        String nombre,
        RolUsuario rol,
        boolean activo
) {
}
