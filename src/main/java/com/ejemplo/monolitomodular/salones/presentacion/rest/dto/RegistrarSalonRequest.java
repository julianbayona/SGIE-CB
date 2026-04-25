package com.ejemplo.monolitomodular.salones.presentacion.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RegistrarSalonRequest(
        @NotBlank(message = "El nombre del salon es obligatorio")
        String nombre,
        @Min(value = 1, message = "La capacidad del salon debe ser mayor a cero")
        int capacidad,
        String descripcion
) {
}
