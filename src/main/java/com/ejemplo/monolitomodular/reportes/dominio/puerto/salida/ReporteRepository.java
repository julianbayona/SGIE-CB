package com.ejemplo.monolitomodular.reportes.dominio.puerto.salida;

import com.ejemplo.monolitomodular.reportes.aplicacion.dto.AnticipoPeriodoView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.AnticiposPorMetodoView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.DemandaSalonView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.EstadoEventoResumenView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.EventosMensualesView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.ReporteFinancieroEventoView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReporteRepository {

    List<EstadoEventoResumenView> contarEventosPorEstado(LocalDateTime desde, LocalDateTime hastaExclusivo);

    List<EventosMensualesView> contarEventosMensuales(LocalDateTime desde, LocalDateTime hastaExclusivo);

    List<ReporteFinancieroEventoView> consultarFinancieroEventos(LocalDateTime desde, LocalDateTime hastaExclusivo);

    List<AnticipoPeriodoView> listarAnticipos(LocalDate desde, LocalDate hasta);

    List<AnticiposPorMetodoView> sumarAnticiposPorMetodo(LocalDate desde, LocalDate hasta);

    List<DemandaSalonView> consultarDemandaSalones(LocalDateTime desde, LocalDateTime hastaExclusivo);
}
