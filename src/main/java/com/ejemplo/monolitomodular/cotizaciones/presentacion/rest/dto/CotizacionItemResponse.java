package com.ejemplo.monolitomodular.cotizaciones.presentacion.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CotizacionItemResponse(
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
