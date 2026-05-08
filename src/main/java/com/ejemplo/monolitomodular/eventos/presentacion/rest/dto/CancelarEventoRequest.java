package com.ejemplo.monolitomodular.eventos.presentacion.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelarEventoRequest(
        @NotBlank(message = "El motivo de cancelacion es obligatorio")
        @Size(max = 500, message = "El motivo de cancelacion no puede superar 500 caracteres")
        String motivo
) {
}
