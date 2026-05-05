package com.ejemplo.monolitomodular.montajes.presentacion.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ConfigurarMontajeRequest(
        String observaciones,
        @NotEmpty(message = "El montaje debe tener al menos una configuracion de mesas")
        List<@Valid MontajeMesaReservaRequest> mesas,
        @NotNull(message = "La infraestructura del montaje es obligatoria")
        @Valid
        InfraestructuraReservaRequest infraestructura,
        List<@Valid AdicionalEventoRequest> adicionales
) {
}
