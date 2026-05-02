package com.ejemplo.monolitomodular.cotizaciones.presentacion.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record GenerarCotizacionRequest(
        @NotNull(message = "El usuario es obligatorio")
        UUID usuarioId,
        @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo")
        BigDecimal descuento,
        String observaciones
) {
}
