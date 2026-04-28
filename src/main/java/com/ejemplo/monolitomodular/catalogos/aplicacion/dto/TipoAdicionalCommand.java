package com.ejemplo.monolitomodular.catalogos.aplicacion.dto;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.ModoCobroAdicional;

import java.math.BigDecimal;

public record TipoAdicionalCommand(
        String nombre,
        ModoCobroAdicional modoCobro,
        BigDecimal precioBase
) {
}
