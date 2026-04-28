package com.ejemplo.monolitomodular.catalogos.aplicacion.dto;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.ModoCobroAdicional;

import java.math.BigDecimal;
import java.util.UUID;

public record TipoAdicionalView(
        UUID id,
        String nombre,
        ModoCobroAdicional modoCobro,
        BigDecimal precioBase,
        boolean activo
) {
}
