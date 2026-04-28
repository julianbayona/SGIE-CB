package com.ejemplo.monolitomodular.catalogos.aplicacion.dto;

import java.util.UUID;

public record CatalogoConColorView(
        UUID id,
        String nombre,
        UUID colorId,
        boolean activo
) {
}
