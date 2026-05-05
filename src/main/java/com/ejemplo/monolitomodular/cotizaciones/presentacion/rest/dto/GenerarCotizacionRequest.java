package com.ejemplo.monolitomodular.cotizaciones.presentacion.rest.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record GenerarCotizacionRequest(
        @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo")
        BigDecimal descuento,
        String observaciones
) {
}
