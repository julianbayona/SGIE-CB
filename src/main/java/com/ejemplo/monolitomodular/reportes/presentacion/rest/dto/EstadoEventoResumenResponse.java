package com.ejemplo.monolitomodular.reportes.presentacion.rest.dto;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;

public record EstadoEventoResumenResponse(
        EstadoEvento estado,
        long total
) {
}
