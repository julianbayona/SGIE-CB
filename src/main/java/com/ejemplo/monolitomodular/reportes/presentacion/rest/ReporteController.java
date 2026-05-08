package com.ejemplo.monolitomodular.reportes.presentacion.rest;

import com.ejemplo.monolitomodular.reportes.aplicacion.dto.AnticipoPeriodoView;
import com.ejemplo.monolitomodular.reportes.aplicacion.dto.AnticiposPorMetodoView;
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
import com.ejemplo.monolitomodular.reportes.presentacion.rest.dto.AnticipoPeriodoResponse;
import com.ejemplo.monolitomodular.reportes.presentacion.rest.dto.AnticiposPorMetodoResponse;
import com.ejemplo.monolitomodular.reportes.presentacion.rest.dto.DemandaSalonResponse;
import com.ejemplo.monolitomodular.reportes.presentacion.rest.dto.EstadoEventoResumenResponse;
import com.ejemplo.monolitomodular.reportes.presentacion.rest.dto.EventosMensualesResponse;
import com.ejemplo.monolitomodular.reportes.presentacion.rest.dto.ReporteAnticiposResponse;
import com.ejemplo.monolitomodular.reportes.presentacion.rest.dto.ReporteFinancieroEventoResponse;
import com.ejemplo.monolitomodular.reportes.presentacion.rest.dto.ResumenEventosResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'TESORERO')")
public class ReporteController {

    private final ConsultarResumenEventosUseCase consultarResumenEventosUseCase;
    private final ConsultarReporteFinancieroEventosUseCase consultarReporteFinancieroEventosUseCase;
    private final ConsultarReporteAnticiposUseCase consultarReporteAnticiposUseCase;
    private final ConsultarDemandaSalonesUseCase consultarDemandaSalonesUseCase;

    public ReporteController(
            ConsultarResumenEventosUseCase consultarResumenEventosUseCase,
            ConsultarReporteFinancieroEventosUseCase consultarReporteFinancieroEventosUseCase,
            ConsultarReporteAnticiposUseCase consultarReporteAnticiposUseCase,
            ConsultarDemandaSalonesUseCase consultarDemandaSalonesUseCase
    ) {
        this.consultarResumenEventosUseCase = consultarResumenEventosUseCase;
        this.consultarReporteFinancieroEventosUseCase = consultarReporteFinancieroEventosUseCase;
        this.consultarReporteAnticiposUseCase = consultarReporteAnticiposUseCase;
        this.consultarDemandaSalonesUseCase = consultarDemandaSalonesUseCase;
    }

    @GetMapping("/eventos/resumen")
    public ResumenEventosResponse resumenEventos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return toResponse(consultarResumenEventosUseCase.consultarResumenEventos(desde, hasta));
    }

    @GetMapping("/eventos/mensual")
    public List<EventosMensualesResponse> eventosMensuales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return consultarResumenEventosUseCase.consultarEventosMensuales(desde, hasta).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/financiero/eventos")
    public List<ReporteFinancieroEventoResponse> financieroEventos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return consultarReporteFinancieroEventosUseCase.consultarFinancieroEventos(desde, hasta).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/anticipos")
    public ReporteAnticiposResponse anticipos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return toResponse(consultarReporteAnticiposUseCase.consultarAnticipos(desde, hasta));
    }

    @GetMapping("/salones/demanda")
    public List<DemandaSalonResponse> demandaSalones(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return consultarDemandaSalonesUseCase.consultarDemandaSalones(desde, hasta).stream()
                .map(this::toResponse)
                .toList();
    }

    private ResumenEventosResponse toResponse(ResumenEventosView view) {
        return new ResumenEventosResponse(
                view.desde(),
                view.hasta(),
                view.totalEventos(),
                view.confirmados(),
                view.cancelados(),
                view.porcentajeConfirmados(),
                view.porcentajeCancelados(),
                view.estados().stream().map(this::toResponse).toList()
        );
    }

    private EstadoEventoResumenResponse toResponse(EstadoEventoResumenView view) {
        return new EstadoEventoResumenResponse(view.estado(), view.total());
    }

    private EventosMensualesResponse toResponse(EventosMensualesView view) {
        return new EventosMensualesResponse(
                view.anio(),
                view.mes(),
                view.confirmados(),
                view.cancelados(),
                view.total()
        );
    }

    private ReporteFinancieroEventoResponse toResponse(ReporteFinancieroEventoView view) {
        return new ReporteFinancieroEventoResponse(
                view.eventoId(),
                view.cliente(),
                view.fechaHoraInicio(),
                view.estado(),
                view.cotizacionId(),
                view.valorTotal(),
                view.totalPagado(),
                view.saldoPendiente(),
                view.pagadoTotalmente()
        );
    }

    private ReporteAnticiposResponse toResponse(ReporteAnticiposView view) {
        return new ReporteAnticiposResponse(
                view.desde(),
                view.hasta(),
                view.cantidad(),
                view.totalRecaudado(),
                view.porMetodo().stream().map(this::toResponse).toList(),
                view.anticipos().stream().map(this::toResponse).toList()
        );
    }

    private AnticiposPorMetodoResponse toResponse(AnticiposPorMetodoView view) {
        return new AnticiposPorMetodoResponse(view.metodoPago(), view.cantidad(), view.total());
    }

    private AnticipoPeriodoResponse toResponse(AnticipoPeriodoView view) {
        return new AnticipoPeriodoResponse(
                view.anticipoId(),
                view.eventoId(),
                view.cliente(),
                view.cotizacionId(),
                view.valor(),
                view.metodoPago(),
                view.fechaPago()
        );
    }

    private DemandaSalonResponse toResponse(DemandaSalonView view) {
        return new DemandaSalonResponse(
                view.salonId(),
                view.salon(),
                view.totalReservas(),
                view.totalEventos(),
                view.totalInvitados()
        );
    }
}
