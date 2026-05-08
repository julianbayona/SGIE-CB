package com.ejemplo.monolitomodular.reportes.aplicacion.servicio;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.AnticipoPeriodoView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.DemandaSalonView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.EstadoEventoResumenView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.EventosMensualesView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.ReporteAnticiposView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.ReporteFinancieroEventoView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.ResumenEventosView;
import com.ejemplo.monolitomodular.reportes.aplicacion.puerto.entrada.ConsultarDemandaSalonesUseCase;
import com.ejemplo.monolitomodular.reportes.aplicacion.puerto.entrada.ConsultarReporteAnticiposUseCase;
import com.ejemplo.monolitomodular.reportes.aplicacion.puerto.entrada.ConsultarReporteFinancieroEventosUseCase;
import com.ejemplo.monolitomodular.reportes.aplicacion.puerto.entrada.ConsultarResumenEventosUseCase;
import com.ejemplo.monolitomodular.reportes.dominio.puerto.salida.ReporteRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteApplicationService implements
        ConsultarResumenEventosUseCase,
        ConsultarReporteFinancieroEventosUseCase,
        ConsultarReporteAnticiposUseCase,
        ConsultarDemandaSalonesUseCase {

    private final ReporteRepository reporteRepository;

    public ReporteApplicationService(ReporteRepository reporteRepository) {
        this.reporteRepository = reporteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenEventosView consultarResumenEventos(LocalDate desde, LocalDate hasta) {
        RangoFechas rango = validarRango(desde, hasta);
        List<EstadoEventoResumenView> estados = reporteRepository.contarEventosPorEstado(rango.desdeInicio(), rango.hastaExclusivo());
        long total = estados.stream().mapToLong(EstadoEventoResumenView::total).sum();
        long confirmados = totalPorEstado(estados, EstadoEvento.CONFIRMADO);
        long cancelados = totalPorEstado(estados, EstadoEvento.CANCELADO);
        return new ResumenEventosView(
                rango.desde(),
                rango.hasta(),
                total,
                confirmados,
                cancelados,
                porcentaje(confirmados, total),
                porcentaje(cancelados, total),
                estados
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventosMensualesView> consultarEventosMensuales(LocalDate desde, LocalDate hasta) {
        RangoFechas rango = validarRango(desde, hasta);
        return reporteRepository.contarEventosMensuales(rango.desdeInicio(), rango.hastaExclusivo());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteFinancieroEventoView> consultarFinancieroEventos(LocalDate desde, LocalDate hasta) {
        RangoFechas rango = validarRango(desde, hasta);
        return reporteRepository.consultarFinancieroEventos(rango.desdeInicio(), rango.hastaExclusivo());
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteAnticiposView consultarAnticipos(LocalDate desde, LocalDate hasta) {
        RangoFechas rango = validarRango(desde, hasta);
        List<AnticipoPeriodoView> anticipos = reporteRepository.listarAnticipos(rango.desde(), rango.hasta());
        BigDecimal totalRecaudado = anticipos.stream()
                .map(AnticipoPeriodoView::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReporteAnticiposView(
                rango.desde(),
                rango.hasta(),
                anticipos.size(),
                totalRecaudado,
                reporteRepository.sumarAnticiposPorMetodo(rango.desde(), rango.hasta()),
                anticipos
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DemandaSalonView> consultarDemandaSalones(LocalDate desde, LocalDate hasta) {
        RangoFechas rango = validarRango(desde, hasta);
        return reporteRepository.consultarDemandaSalones(rango.desdeInicio(), rango.hastaExclusivo());
    }

    private static long totalPorEstado(List<EstadoEventoResumenView> estados, EstadoEvento estado) {
        return estados.stream()
                .filter(item -> item.estado() == estado)
                .mapToLong(EstadoEventoResumenView::total)
                .findFirst()
                .orElse(0);
    }

    private static BigDecimal porcentaje(long cantidad, long total) {
        if (total == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(cantidad)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }

    private static RangoFechas validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null) {
            throw new DomainException("La fecha inicial del reporte es obligatoria");
        }
        if (hasta == null) {
            throw new DomainException("La fecha final del reporte es obligatoria");
        }
        if (hasta.isBefore(desde)) {
            throw new DomainException("La fecha final del reporte no puede ser anterior a la fecha inicial");
        }
        return new RangoFechas(desde, hasta);
    }

    private record RangoFechas(LocalDate desde, LocalDate hasta) {

        LocalDateTime desdeInicio() {
            return desde.atStartOfDay();
        }

        LocalDateTime hastaExclusivo() {
            return hasta.plusDays(1).atStartOfDay();
        }
    }
}
