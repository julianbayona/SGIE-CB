package com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto;

import java.util.UUID;

public record CatalogoBasicoResponse(
        UUID id,
        String nombre,
        String descripcion,
        boolean activo
) {
}
