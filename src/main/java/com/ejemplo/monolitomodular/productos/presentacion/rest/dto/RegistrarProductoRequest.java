package com.ejemplo.monolitomodular.productos.presentacion.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegistrarProductoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
        BigDecimal precio
) {
}
