package com.ejemplo.monolitomodular.catalogos.aplicacion.dto;

import java.util.UUID;

public record ColorView(
        UUID id,
        String nombre,
        String codigoHex,
        boolean activo
) {
}
