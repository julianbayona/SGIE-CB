package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.PlatoView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.ConsultarPlatoUseCase;
import com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto.PlatoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalogos/platos")
public class PlatoController {

    private final ConsultarPlatoUseCase consultarPlatoUseCase;

    public PlatoController(ConsultarPlatoUseCase consultarPlatoUseCase) {
        this.consultarPlatoUseCase = consultarPlatoUseCase;
    }

    @GetMapping
    public List<PlatoResponse> listar() {
        return consultarPlatoUseCase.listarActivos().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public PlatoResponse obtenerPorId(@PathVariable UUID id) {
        return toResponse(consultarPlatoUseCase.obtenerPorId(id));
    }

    private PlatoResponse toResponse(PlatoView view) {
        return new PlatoResponse(
                view.id(),
                view.nombre(),
                view.descripcion(),
                view.precioBase(),
                view.activo()
        );
    }
}
