package com.ejemplo.monolitomodular.reportes.aplicacion.dto;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;

public record EstadoEventoResumenView(
        EstadoEvento estado,
        long total
) {
}
