package com.ejemplo.monolitomodular.pruebasplato.aplicacion.dto;

import com.ejemplo.monolitomodular.pruebasplato.dominio.modelo.EstadoPruebaPlato;

import java.time.LocalDateTime;
import java.util.UUID;

public record PruebaPlatoView(
        UUID id,
        UUID eventoId,
        LocalDateTime fechaRealizacion,
        EstadoPruebaPlato estado
) {
}
