package com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto;

import java.util.UUID;

public record ColorResponse(
        UUID id,
        String nombre,
        String codigoHex,
        boolean activo
) {
}
