package com.ejemplo.monolitomodular.reportes.aplicacion.dto;

import java.math.BigDecimal;

public record AnticiposPorMetodoView(
        String metodoPago,
        long cantidad,
        BigDecimal total
) {
}
