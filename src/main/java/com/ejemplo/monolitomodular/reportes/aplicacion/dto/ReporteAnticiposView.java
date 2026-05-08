package com.ejemplo.monolitomodular.reportes.aplicacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReporteAnticiposView(
        LocalDate desde,
        LocalDate hasta,
        long cantidad,
        BigDecimal totalRecaudado,
        List<AnticiposPorMetodoView> porMetodo,
        List<AnticipoPeriodoView> anticipos
) {
}
