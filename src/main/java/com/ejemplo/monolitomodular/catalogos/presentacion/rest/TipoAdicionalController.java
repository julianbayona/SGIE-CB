package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.TipoAdicionalCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.TipoAdicionalView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarTipoAdicionalUseCase;
import com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto.TipoAdicionalRequest;
import com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto.TipoAdicionalResponse;
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
@RequestMapping("/api/catalogos/tipos-adicional")
public class TipoAdicionalController {

    private final GestionarTipoAdicionalUseCase gestionarTipoAdicionalUseCase;

    public TipoAdicionalController(GestionarTipoAdicionalUseCase gestionarTipoAdicionalUseCase) {
        this.gestionarTipoAdicionalUseCase = gestionarTipoAdicionalUseCase;
    }

    @PostMapping
    public ResponseEntity<TipoAdicionalResponse> crear(@Valid @RequestBody TipoAdicionalRequest request) {
        TipoAdicionalView tipoAdicional = gestionarTipoAdicionalUseCase.crearTipoAdicional(toCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tipoAdicional.id())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(tipoAdicional));
    }

    @PutMapping("/{id}")
    public TipoAdicionalResponse actualizar(@PathVariable UUID id, @Valid @RequestBody TipoAdicionalRequest request) {
        return toResponse(gestionarTipoAdicionalUseCase.actualizarTipoAdicional(id, toCommand(request)));
    }

    @DeleteMapping("/{id}")
    public TipoAdicionalResponse desactivar(@PathVariable UUID id) {
        return toResponse(gestionarTipoAdicionalUseCase.desactivarTipoAdicional(id));
    }

    @GetMapping("/{id}")
    public TipoAdicionalResponse obtenerPorId(@PathVariable UUID id) {
        return toResponse(gestionarTipoAdicionalUseCase.obtenerTipoAdicional(id));
    }

    @GetMapping
    public List<TipoAdicionalResponse> listar() {
        return gestionarTipoAdicionalUseCase.listarTiposAdicional().stream().map(this::toResponse).toList();
    }

    private TipoAdicionalCommand toCommand(TipoAdicionalRequest request) {
        return new TipoAdicionalCommand(request.nombre(), request.modoCobro(), request.precioBase());
    }

    private TipoAdicionalResponse toResponse(TipoAdicionalView view) {
        return new TipoAdicionalResponse(view.id(), view.nombre(), view.modoCobro(), view.precioBase(), view.activo());
    }
}
