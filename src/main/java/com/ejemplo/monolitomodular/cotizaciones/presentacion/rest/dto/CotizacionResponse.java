package com.ejemplo.monolitomodular.cotizaciones.presentacion.rest.dto;

import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CotizacionResponse(
        UUID id,
        UUID reservaId,
        UUID usuarioId,
        EstadoCotizacion estado,
        BigDecimal valorSubtotal,
        BigDecimal descuento,
        BigDecimal valorTotal,
        String observaciones,
        List<CotizacionItemResponse> items
) {
}
