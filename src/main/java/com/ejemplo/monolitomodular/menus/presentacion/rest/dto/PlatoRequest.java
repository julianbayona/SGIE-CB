package com.ejemplo.monolitomodular.menus.presentacion.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PlatoRequest(
        @NotBlank @Size(max = 120) String nombre,
        @Size(max = 500) String descripcion,
        @NotNull @DecimalMin("0.00") BigDecimal precioBase
) {
}
