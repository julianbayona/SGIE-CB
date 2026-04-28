package com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.ModoCobroAdicional;

import java.math.BigDecimal;
import java.util.UUID;

public record TipoAdicionalResponse(
        UUID id,
        String nombre,
        ModoCobroAdicional modoCobro,
        BigDecimal precioBase,
        boolean activo
) {
}
