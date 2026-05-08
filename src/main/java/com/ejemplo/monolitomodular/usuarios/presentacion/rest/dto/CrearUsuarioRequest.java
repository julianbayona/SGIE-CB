package com.ejemplo.monolitomodular.usuarios.presentacion.rest.dto;

import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearUsuarioRequest(
        @NotBlank
        @Size(max = 120)
        String nombre,

        @NotBlank
        @Size(min = 6, max = 80)
        String contrasena,

        @NotNull
        RolUsuario rol
) {
}
