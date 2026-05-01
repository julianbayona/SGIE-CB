package com.ejemplo.monolitomodular.montajes.presentacion.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MontajeMesaReservaRequest(
        @NotNull(message = "El tipo de mesa es obligatorio")
        UUID tipoMesaId,
        @NotNull(message = "El tipo de silla es obligatorio")
        UUID tipoSillaId,
        @Min(value = 1, message = "La cantidad de sillas por mesa debe ser mayor a cero")
        int sillaPorMesa,
        @Min(value = 1, message = "La cantidad de mesas debe ser mayor a cero")
        int cantidadMesas,
        @NotNull(message = "El mantel es obligatorio")
        UUID mantelId,
        UUID sobremantelId,
        boolean vajilla,
        boolean fajon
) {
}
