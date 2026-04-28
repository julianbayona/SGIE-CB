package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoConColorView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarSobremantelUseCase;
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
@RequestMapping("/api/catalogos/sobremanteles")
public class SobremantelController {

    private final GestionarSobremantelUseCase gestionarSobremantelUseCase;

    public SobremantelController(GestionarSobremantelUseCase gestionarSobremantelUseCase) {
        this.gestionarSobremantelUseCase = gestionarSobremantelUseCase;
    }

    @PostMapping
    public ResponseEntity<CatalogoConColorResponse> crear(@Valid @RequestBody CatalogoConColorRequest request) {
        CatalogoConColorView sobremantel = gestionarSobremantelUseCase.crearSobremantel(toCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(sobremantel.id())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(sobremantel));
    }

    @PutMapping("/{id}")
    public CatalogoConColorResponse actualizar(@PathVariable UUID id, @Valid @RequestBody CatalogoConColorRequest request) {
        return toResponse(gestionarSobremantelUseCase.actualizarSobremantel(id, toCommand(request)));
    }

    @DeleteMapping("/{id}")
    public CatalogoConColorResponse desactivar(@PathVariable UUID id) {
        return toResponse(gestionarSobremantelUseCase.desactivarSobremantel(id));
    }

    @GetMapping("/{id}")
    public CatalogoConColorResponse obtenerPorId(@PathVariable UUID id) {
        return toResponse(gestionarSobremantelUseCase.obtenerSobremantel(id));
    }

    @GetMapping
    public List<CatalogoConColorResponse> listar() {
        return gestionarSobremantelUseCase.listarSobremanteles().stream().map(this::toResponse).toList();
    }

    private CatalogoConColorCommand toCommand(CatalogoConColorRequest request) {
        return new CatalogoConColorCommand(request.nombre(), request.colorId());
    }

    private CatalogoConColorResponse toResponse(CatalogoConColorView view) {
        return new CatalogoConColorResponse(view.id(), view.nombre(), view.colorId(), view.activo());
    }
}
