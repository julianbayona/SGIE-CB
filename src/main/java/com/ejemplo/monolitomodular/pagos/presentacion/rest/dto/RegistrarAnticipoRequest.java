package com.ejemplo.monolitomodular.pagos.presentacion.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarAnticipoRequest(
        @NotNull UUID usuarioId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal valor,
        @NotBlank String metodoPago,
        @NotNull LocalDate fechaPago,
        String observaciones
) {
}
