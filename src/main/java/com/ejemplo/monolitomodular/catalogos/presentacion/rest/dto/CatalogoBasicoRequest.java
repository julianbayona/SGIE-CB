package com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CatalogoBasicoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        String descripcion
) {
}
