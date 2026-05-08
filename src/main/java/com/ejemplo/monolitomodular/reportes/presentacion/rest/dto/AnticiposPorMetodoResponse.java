package com.ejemplo.monolitomodular.reportes.presentacion.rest.dto;

import java.math.BigDecimal;

public record AnticiposPorMetodoResponse(
        String metodoPago,
        long cantidad,
        BigDecimal total
) {
}
