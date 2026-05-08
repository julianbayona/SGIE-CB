package com.ejemplo.monolitomodular.reportes.presentacion.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ResumenEventosResponse(
        LocalDate desde,
        LocalDate hasta,
        long totalEventos,
        long confirmados,
        long cancelados,
        BigDecimal porcentajeConfirmados,
        BigDecimal porcentajeCancelados,
        List<EstadoEventoResumenResponse> estados
) {
}
