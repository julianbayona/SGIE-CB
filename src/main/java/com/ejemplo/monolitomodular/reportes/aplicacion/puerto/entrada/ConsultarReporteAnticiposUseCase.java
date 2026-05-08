package com.ejemplo.monolitomodular.reportes.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.reportes.aplicacion.dto.ReporteAnticiposView;

import java.time.LocalDate;

public interface ConsultarReporteAnticiposUseCase {

    ReporteAnticiposView consultarAnticipos(LocalDate desde, LocalDate hasta);
}
