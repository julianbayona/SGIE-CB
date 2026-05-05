package com.ejemplo.monolitomodular.menus.presentacion.rest;

import com.ejemplo.monolitomodular.menus.aplicacion.dto.PlatoMomentoCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.PlatoMomentoView;
import com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada.GestionarPlatoMomentoUseCase;
import com.ejemplo.monolitomodular.menus.presentacion.rest.dto.PlatoMomentoRequest;
import com.ejemplo.monolitomodular.menus.presentacion.rest.dto.PlatoMomentoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalogos/plato-momentos")
public class PlatoMomentoController {

    private final GestionarPlatoMomentoUseCase useCase;

    public PlatoMomentoController(GestionarPlatoMomentoUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlatoMomentoResponse asociar(@Valid @RequestBody PlatoMomentoRequest request) {
        return toResponse(useCase.asociar(new PlatoMomentoCommand(request.platoId(), request.tipoMomentoId())));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@RequestParam UUID platoId, @RequestParam UUID tipoMomentoId) {
        useCase.eliminar(platoId, tipoMomentoId);
    }

    @GetMapping
    public List<PlatoMomentoResponse> listar(
            @RequestParam(required = false) UUID platoId,
            @RequestParam(required = false) UUID tipoMomentoId
    ) {
        return useCase.listar(platoId, tipoMomentoId).stream().map(this::toResponse).toList();
    }

    private PlatoMomentoResponse toResponse(PlatoMomentoView view) {
        return new PlatoMomentoResponse(view.platoId(), view.tipoMomentoId());
    }
}
