package com.ejemplo.monolitomodular.reportes.aplicacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ResumenEventosView(
        LocalDate desde,
        LocalDate hasta,
        long totalEventos,
        long confirmados,
        long cancelados,
        BigDecimal porcentajeConfirmados,
        BigDecimal porcentajeCancelados,
        List<EstadoEventoResumenView> estados
) {
}
