package com.ejemplo.monolitomodular.montajes.presentacion.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AdicionalEventoRequest(
        @NotNull(message = "El tipo adicional es obligatorio")
        UUID tipoAdicionalId,
        @Min(value = 1, message = "La cantidad del adicional debe ser mayor a cero")
        int cantidad,
        @DecimalMin(value = "0.00", message = "El precio override no puede ser negativo")
        BigDecimal precioOverride
) {
}
