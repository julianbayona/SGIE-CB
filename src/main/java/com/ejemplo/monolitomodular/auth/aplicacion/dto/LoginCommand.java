package com.ejemplo.monolitomodular.auth.aplicacion.dto;

public record LoginCommand(
        String nombre,
        String contrasena
) {
}
