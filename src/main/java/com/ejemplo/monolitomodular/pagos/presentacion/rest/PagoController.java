package com.ejemplo.monolitomodular.pagos.presentacion.rest;

import com.ejemplo.monolitomodular.auth.infraestructura.seguridad.UsuarioAutenticado;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.AnticipoView;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.EstadoFinancieroEventoView;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.ProgramarRecordatorioAnticipoCommand;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.RegistrarAnticipoCommand;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.RecordatorioAnticipoView;
import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.ConsultarAnticiposUseCase;
import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.ConsultarEstadoFinancieroEventoUseCase;
import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.ProcesarRecordatoriosAnticipoProgramadosUseCase;
import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.ProgramarRecordatorioAnticipoUseCase;
import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.RegistrarAnticipoUseCase;
import com.ejemplo.monolitomodular.pagos.presentacion.rest.dto.AnticipoResponse;
import com.ejemplo.monolitomodular.pagos.presentacion.rest.dto.EstadoFinancieroEventoResponse;
import com.ejemplo.monolitomodular.pagos.presentacion.rest.dto.ProgramarRecordatorioAnticipoRequest;
import com.ejemplo.monolitomodular.pagos.presentacion.rest.dto.RegistrarAnticipoRequest;
import com.ejemplo.monolitomodular.pagos.presentacion.rest.dto.RecordatorioAnticipoResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PagoController {

    private final RegistrarAnticipoUseCase registrarAnticipoUseCase;
    private final ConsultarAnticiposUseCase consultarAnticiposUseCase;
    private final ConsultarEstadoFinancieroEventoUseCase consultarEstadoFinancieroEventoUseCase;
    private final ProgramarRecordatorioAnticipoUseCase programarRecordatorioAnticipoUseCase;
    private final ProcesarRecordatoriosAnticipoProgramadosUseCase procesarRecordatoriosAnticipoProgramadosUseCase;

    public PagoController(
            RegistrarAnticipoUseCase registrarAnticipoUseCase,
            ConsultarAnticiposUseCase consultarAnticiposUseCase,
            ConsultarEstadoFinancieroEventoUseCase consultarEstadoFinancieroEventoUseCase,
            ProgramarRecordatorioAnticipoUseCase programarRecordatorioAnticipoUseCase,
            ProcesarRecordatoriosAnticipoProgramadosUseCase procesarRecordatoriosAnticipoProgramadosUseCase
    ) {
        this.registrarAnticipoUseCase = registrarAnticipoUseCase;
        this.consultarAnticiposUseCase = consultarAnticiposUseCase;
        this.consultarEstadoFinancieroEventoUseCase = consultarEstadoFinancieroEventoUseCase;
        this.programarRecordatorioAnticipoUseCase = programarRecordatorioAnticipoUseCase;
        this.procesarRecordatoriosAnticipoProgramadosUseCase = procesarRecordatoriosAnticipoProgramadosUseCase;
    }

    @GetMapping("/cotizaciones/{cotizacionId}/anticipos")
    public List<AnticipoResponse> listarAnticipos(@PathVariable UUID cotizacionId) {
        return consultarAnticiposUseCase.listarPorCotizacion(cotizacionId).stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/eventos/{eventoId}/estado-financiero")
    public EstadoFinancieroEventoResponse estadoFinanciero(@PathVariable UUID eventoId) {
        return toResponse(consultarEstadoFinancieroEventoUseCase.consultar(eventoId));
    }

    @PostMapping("/cotizaciones/{cotizacionId}/anticipos")
    public AnticipoResponse registrar(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable UUID cotizacionId,
            @Valid @RequestBody RegistrarAnticipoRequest request
    ) {
        return toResponse(registrarAnticipoUseCase.ejecutar(new RegistrarAnticipoCommand(
                cotizacionId,
                usuario.id(),
                request.valor(),
                request.metodoPago(),
                request.fechaPago(),
                request.observaciones()
        )));
    }

    @PostMapping("/recordatorios-anticipo/procesar-pendientes")
    public int procesarRecordatoriosPendientes(
            @RequestParam(defaultValue = "50") int limite
    ) {
        return procesarRecordatoriosAnticipoProgramadosUseCase.procesar(limite);
    }

    @PostMapping("/eventos/{eventoId}/recordatorios-anticipo")
    public RecordatorioAnticipoResponse programarRecordatorio(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable UUID eventoId,
            @Valid @RequestBody ProgramarRecordatorioAnticipoRequest request
    ) {
        return toResponse(programarRecordatorioAnticipoUseCase.ejecutar(new ProgramarRecordatorioAnticipoCommand(
                eventoId,
                usuario.id(),
                request.fechaRecordatorio()
        )));
    }

    private AnticipoResponse toResponse(AnticipoView view) {
        return new AnticipoResponse(
                view.id(),
                view.cotizacionId(),
                view.usuarioId(),
                view.valor(),
                view.metodoPago(),
                view.fechaPago(),
                view.observaciones(),
                view.totalPagado(),
                view.saldoPendiente()
        );
    }

    private RecordatorioAnticipoResponse toResponse(RecordatorioAnticipoView view) {
        return new RecordatorioAnticipoResponse(
                view.id(),
                view.eventoId(),
                view.usuarioId(),
                view.fechaRecordatorio(),
                view.estado(),
                view.notificacionId()
        );
    }

    private EstadoFinancieroEventoResponse toResponse(EstadoFinancieroEventoView view) {
        return new EstadoFinancieroEventoResponse(
                view.eventoId(),
                view.cotizacionVigenteId(),
                view.valorTotal(),
                view.totalPagado(),
                view.saldoPendiente(),
                view.pagadoTotalmente()
        );
    }
}
