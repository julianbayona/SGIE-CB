package com.ejemplo.monolitomodular.cotizaciones.presentacion.rest.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record ActualizarItemCotizacionRequest(
        @DecimalMin(value = "0.00", message = "El precio override no puede ser negativo")
        BigDecimal precioOverride
) {
}
