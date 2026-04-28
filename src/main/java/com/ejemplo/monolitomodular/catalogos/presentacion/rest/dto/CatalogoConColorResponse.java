package com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto;

import java.util.UUID;

public record CatalogoConColorResponse(
        UUID id,
        String nombre,
        UUID colorId,
        boolean activo
) {
}
