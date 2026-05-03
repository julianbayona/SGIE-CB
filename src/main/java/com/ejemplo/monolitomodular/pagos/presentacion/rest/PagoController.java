package com.ejemplo.monolitomodular.pagos.presentacion.rest;

import com.ejemplo.monolitomodular.pagos.aplicacion.dto.AnticipoView;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.RegistrarAnticipoCommand;
import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.RegistrarAnticipoUseCase;
import com.ejemplo.monolitomodular.pagos.presentacion.rest.dto.AnticipoResponse;
import com.ejemplo.monolitomodular.pagos.presentacion.rest.dto.RegistrarAnticipoRequest;
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

    public PagoController(RegistrarAnticipoUseCase registrarAnticipoUseCase) {
        this.registrarAnticipoUseCase = registrarAnticipoUseCase;
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
}
