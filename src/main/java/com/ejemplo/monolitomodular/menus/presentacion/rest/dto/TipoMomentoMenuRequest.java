package com.ejemplo.monolitomodular.menus.presentacion.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoMomentoMenuRequest(
        @NotBlank @Size(max = 120) String nombre
) {
}
