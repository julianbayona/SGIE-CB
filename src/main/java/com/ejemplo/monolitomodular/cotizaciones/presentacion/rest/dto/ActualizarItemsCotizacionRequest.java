package com.ejemplo.monolitomodular.cotizaciones.presentacion.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ActualizarItemsCotizacionRequest(
        @NotEmpty List<@Valid Item> items
) {

    public record Item(
            @NotNull UUID itemId,
            @DecimalMin(value = "0.00", message = "El precio override no puede ser negativo")
            BigDecimal precioOverride
    ) {
    }
}
