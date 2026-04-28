package com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.ModoCobroAdicional;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TipoAdicionalRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        @NotNull(message = "El modo de cobro es obligatorio")
        ModoCobroAdicional modoCobro,
        @NotNull(message = "El precio base es obligatorio")
        @DecimalMin(value = "0.00", message = "El precio base no puede ser negativo")
        BigDecimal precioBase
) {
}
