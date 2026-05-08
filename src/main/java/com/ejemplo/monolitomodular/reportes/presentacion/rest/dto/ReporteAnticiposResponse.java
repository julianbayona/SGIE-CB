package com.ejemplo.monolitomodular.reportes.presentacion.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReporteAnticiposResponse(
        LocalDate desde,
        LocalDate hasta,
        long cantidad,
        BigDecimal totalRecaudado,
        List<AnticiposPorMetodoResponse> porMetodo,
        List<AnticipoPeriodoResponse> anticipos
) {
}
