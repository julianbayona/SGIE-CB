package com.ejemplo.monolitomodular.pruebasplato.presentacion.rest;

import com.ejemplo.monolitomodular.pruebasplato.aplicacion.dto.ProgramarPruebaPlatoCommand;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.dto.PruebaPlatoView;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.puerto.entrada.ProgramarPruebaPlatoUseCase;
import com.ejemplo.monolitomodular.pruebasplato.presentacion.rest.dto.ProgramarPruebaPlatoRequest;
import com.ejemplo.monolitomodular.pruebasplato.presentacion.rest.dto.PruebaPlatoResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PruebaPlatoController {

    private final ProgramarPruebaPlatoUseCase programarPruebaPlatoUseCase;

    public PruebaPlatoController(ProgramarPruebaPlatoUseCase programarPruebaPlatoUseCase) {
        this.programarPruebaPlatoUseCase = programarPruebaPlatoUseCase;
    }

    @PostMapping("/eventos/{eventoId}/pruebas-plato")
    public PruebaPlatoResponse programar(
            @PathVariable UUID eventoId,
            @Valid @RequestBody ProgramarPruebaPlatoRequest request
    ) {
        return toResponse(programarPruebaPlatoUseCase.ejecutar(new ProgramarPruebaPlatoCommand(
                eventoId,
                request.usuarioId(),
                request.fechaRealizacion()
        )));
    }

    private PruebaPlatoResponse toResponse(PruebaPlatoView view) {
        return new PruebaPlatoResponse(
                view.id(),
                view.eventoId(),
                view.fechaRealizacion(),
                view.estado()
        );
    }
}
