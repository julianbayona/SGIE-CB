package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarMantelUseCase;
import com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto.CatalogoConColorRequest;
import com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto.CatalogoConColorResponse;
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
@RequestMapping("/api/catalogos/manteles")
public class MantelController {

    private final GestionarMantelUseCase gestionarMantelUseCase;

    public MantelController(GestionarMantelUseCase gestionarMantelUseCase) {
        this.gestionarMantelUseCase = gestionarMantelUseCase;
    }

    @PostMapping
    public ResponseEntity<CatalogoConColorResponse> crear(@Valid @RequestBody CatalogoConColorRequest request) {
        CatalogoConColorView mantel = gestionarMantelUseCase.crearMantel(toCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(mantel.id())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(mantel));
    }

    @PutMapping("/{id}")
    public CatalogoConColorResponse actualizar(@PathVariable UUID id, @Valid @RequestBody CatalogoConColorRequest request) {
        return toResponse(gestionarMantelUseCase.actualizarMantel(id, toCommand(request)));
    }

    @DeleteMapping("/{id}")
    public CatalogoConColorResponse desactivar(@PathVariable UUID id) {
        return toResponse(gestionarMantelUseCase.desactivarMantel(id));
    }

    @GetMapping("/{id}")
    public CatalogoConColorResponse obtenerPorId(@PathVariable UUID id) {
        return toResponse(gestionarMantelUseCase.obtenerMantel(id));
    }

    @GetMapping
    public List<CatalogoConColorResponse> listar() {
        return gestionarMantelUseCase.listarManteles().stream().map(this::toResponse).toList();
    }

    private CatalogoConColorCommand toCommand(CatalogoConColorRequest request) {
        return new CatalogoConColorCommand(request.nombre(), request.colorId());
    }

    private CatalogoConColorResponse toResponse(CatalogoConColorView view) {
        return new CatalogoConColorResponse(view.id(), view.nombre(), view.colorId(), view.activo());
    }
}
