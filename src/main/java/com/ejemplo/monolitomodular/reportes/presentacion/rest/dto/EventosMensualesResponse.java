package com.ejemplo.monolitomodular.reportes.presentacion.rest.dto;

public record EventosMensualesResponse(
        int anio,
        int mes,
        long confirmados,
        long cancelados,
        long total
) {
}
