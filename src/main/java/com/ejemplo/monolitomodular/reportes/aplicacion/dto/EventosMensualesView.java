package com.ejemplo.monolitomodular.reportes.aplicacion.dto;

public record EventosMensualesView(
        int anio,
        int mes,
        long confirmados,
        long cancelados,
        long total
) {
}
