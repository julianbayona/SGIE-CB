package com.ejemplo.monolitomodular.pruebasplato.presentacion.rest.dto;

import com.ejemplo.monolitomodular.pruebasplato.dominio.modelo.EstadoPruebaPlato;

import java.time.LocalDateTime;
import java.util.UUID;

public record PruebaPlatoResponse(
        UUID id,
        UUID eventoId,
        LocalDateTime fechaRealizacion,
        EstadoPruebaPlato estado
) {
}
