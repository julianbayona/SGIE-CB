package com.ejemplo.monolitomodular.reportes.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.reportes.aplicacion.dto.ResumenEventosView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.EventosMensualesView;

import java.time.LocalDate;
import java.util.List;

public interface ConsultarResumenEventosUseCase {

    ResumenEventosView consultarResumenEventos(LocalDate desde, LocalDate hasta);

    List<EventosMensualesView> consultarEventosMensuales(LocalDate desde, LocalDate hasta);
}
