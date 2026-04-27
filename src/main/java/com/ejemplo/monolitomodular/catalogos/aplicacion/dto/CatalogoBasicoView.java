package com.ejemplo.monolitomodular.catalogos.aplicacion.dto;

import java.util.UUID;

public record CatalogoBasicoView(
        UUID id,
        String nombre,
        String descripcion,
        boolean activo
) {
}
