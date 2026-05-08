package com.ejemplo.monolitomodular.usuarios.aplicacion.dto;

import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;

public record CrearUsuarioCommand(
        String nombre,
        String contrasena,
        RolUsuario rol
) {
}
