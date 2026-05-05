package com.ejemplo.monolitomodular.pagos.presentacion.rest;

import com.ejemplo.monolitomodular.pagos.aplicacion.dto.AnticipoView;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.ProgramarRecordatorioAnticipoCommand;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.RegistrarAnticipoCommand;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.RecordatorioAnticipoView;
import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.ProgramarRecordatorioAnticipoUseCase;
import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.RegistrarAnticipoUseCase;
import com.ejemplo.monolitomodular.pagos.presentacion.rest.dto.AnticipoResponse;
import com.ejemplo.monolitomodular.pagos.presentacion.rest.dto.ProgramarRecordatorioAnticipoRequest;
import com.ejemplo.monolitomodular.pagos.presentacion.rest.dto.RegistrarAnticipoRequest;
import com.ejemplo.monolitomodular.pagos.presentacion.rest.dto.RecordatorioAnticipoResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PagoController {

    private final RegistrarAnticipoUseCase registrarAnticipoUseCase;
    private final ProgramarRecordatorioAnticipoUseCase programarRecordatorioAnticipoUseCase;

    public PagoController(
            RegistrarAnticipoUseCase registrarAnticipoUseCase,
            ProgramarRecordatorioAnticipoUseCase programarRecordatorioAnticipoUseCase
    ) {
        this.registrarAnticipoUseCase = registrarAnticipoUseCase;
        this.programarRecordatorioAnticipoUseCase = programarRecordatorioAnticipoUseCase;
    }

    @PostMapping("/cotizaciones/{cotizacionId}/anticipos")
    public AnticipoResponse registrar(
            @PathVariable UUID cotizacionId,
            @Valid @RequestBody RegistrarAnticipoRequest request
    ) {
        return toResponse(registrarAnticipoUseCase.ejecutar(new RegistrarAnticipoCommand(
                cotizacionId,
                request.usuarioId(),
                request.valor(),
                request.metodoPago(),
                request.fechaPago(),
                request.observaciones()
        )));
    }

    @PostMapping("/eventos/{eventoId}/recordatorios-anticipo")
    public RecordatorioAnticipoResponse programarRecordatorio(
            @PathVariable UUID eventoId,
            @Valid @RequestBody ProgramarRecordatorioAnticipoRequest request
    ) {
        return toResponse(programarRecordatorioAnticipoUseCase.ejecutar(new ProgramarRecordatorioAnticipoCommand(
                eventoId,
                request.usuarioId(),
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
}
