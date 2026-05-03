package com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CotizacionItemView(
        UUID id,
        String tipoConcepto,
        UUID origenId,
        String descripcion,
        BigDecimal precioBase,
        BigDecimal precioOverride,
        int cantidad,
        BigDecimal subtotal
) {
}
