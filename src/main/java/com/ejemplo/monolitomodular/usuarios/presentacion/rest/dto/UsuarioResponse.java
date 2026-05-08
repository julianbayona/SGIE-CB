package com.ejemplo.monolitomodular.usuarios.presentacion.rest.dto;

import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;

import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nombre,
        RolUsuario rol,
        boolean activo
) {
}
