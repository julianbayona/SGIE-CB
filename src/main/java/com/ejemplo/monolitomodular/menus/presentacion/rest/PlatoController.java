package com.ejemplo.monolitomodular.menus.presentacion.rest;

import com.ejemplo.monolitomodular.menus.aplicacion.dto.PlatoCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.PlatoView;
import com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada.GestionarPlatoUseCase;
import com.ejemplo.monolitomodular.menus.presentacion.rest.dto.PlatoRequest;
import com.ejemplo.monolitomodular.menus.presentacion.rest.dto.PlatoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalogos/platos")
public class PlatoController {

    private final GestionarPlatoUseCase gestionarPlatoUseCase;

    public PlatoController(GestionarPlatoUseCase gestionarPlatoUseCase) {
        this.gestionarPlatoUseCase = gestionarPlatoUseCase;
    }

    @PostMapping
    public ResponseEntity<PlatoResponse> crear(@Valid @RequestBody PlatoRequest request) {
        PlatoView plato = gestionarPlatoUseCase.crear(toCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(plato.id())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(plato));
    }

    @PutMapping("/{id}")
    public PlatoResponse actualizar(@PathVariable UUID id, @Valid @RequestBody PlatoRequest request) {
        return toResponse(gestionarPlatoUseCase.actualizar(id, toCommand(request)));
    }

    @DeleteMapping("/{id}")
    public PlatoResponse desactivar(@PathVariable UUID id) {
        return toResponse(gestionarPlatoUseCase.desactivar(id));
    }

    @GetMapping("/{id}")
    public PlatoResponse obtener(@PathVariable UUID id) {
        return toResponse(gestionarPlatoUseCase.obtener(id));
    }

    @GetMapping
    public List<PlatoResponse> listar() {
        return gestionarPlatoUseCase.listar().stream().map(this::toResponse).toList();
    }

    private PlatoCommand toCommand(PlatoRequest request) {
        return new PlatoCommand(request.nombre(), request.descripcion(), request.precioBase());
    }

    private PlatoResponse toResponse(PlatoView view) {
        return new PlatoResponse(view.id(), view.nombre(), view.descripcion(), view.precioBase(), view.activo());
    }
}
