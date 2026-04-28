package com.ejemplo.monolitomodular.catalogos.aplicacion.dto;

import java.util.UUID;

public record CatalogoConColorCommand(
        String nombre,
        UUID colorId
) {
}
