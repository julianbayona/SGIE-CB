package com.ejemplo.monolitomodular.reportes.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.reportes.aplicacion.dto.ReporteFinancieroEventoView;

import java.time.LocalDate;
import java.util.List;

public interface ConsultarReporteFinancieroEventosUseCase {

    List<ReporteFinancieroEventoView> consultarFinancieroEventos(LocalDate desde, LocalDate hasta);
}
