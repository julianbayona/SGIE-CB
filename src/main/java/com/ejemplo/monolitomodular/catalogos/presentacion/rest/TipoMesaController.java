package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.CatalogoBasicoView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarTipoMesaUseCase;
import com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto.CatalogoBasicoRequest;
import com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto.CatalogoBasicoResponse;
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
@RequestMapping("/api/catalogos/tipos-mesa")
public class TipoMesaController {

    private final GestionarTipoMesaUseCase gestionarTipoMesaUseCase;

    public TipoMesaController(GestionarTipoMesaUseCase gestionarTipoMesaUseCase) {
        this.gestionarTipoMesaUseCase = gestionarTipoMesaUseCase;
    }

    @PostMapping
    public ResponseEntity<CatalogoBasicoResponse> crear(@Valid @RequestBody CatalogoBasicoRequest request) {
        CatalogoBasicoView tipoMesa = gestionarTipoMesaUseCase.crearTipoMesa(toCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tipoMesa.id())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(tipoMesa));
    }

    @PutMapping("/{id}")
    public CatalogoBasicoResponse actualizar(@PathVariable UUID id, @Valid @RequestBody CatalogoBasicoRequest request) {
        return toResponse(gestionarTipoMesaUseCase.actualizarTipoMesa(id, toCommand(request)));
    }

    @DeleteMapping("/{id}")
    public CatalogoBasicoResponse desactivar(@PathVariable UUID id) {
        return toResponse(gestionarTipoMesaUseCase.desactivarTipoMesa(id));
    }

    @GetMapping("/{id}")
    public CatalogoBasicoResponse obtenerPorId(@PathVariable UUID id) {
        return toResponse(gestionarTipoMesaUseCase.obtenerTipoMesa(id));
    }

    @GetMapping
    public List<CatalogoBasicoResponse> listar() {
        return gestionarTipoMesaUseCase.listarTiposMesa().stream().map(this::toResponse).toList();
    }

    private CatalogoBasicoCommand toCommand(CatalogoBasicoRequest request) {
        return new CatalogoBasicoCommand(request.nombre(), request.descripcion());
    }

    private CatalogoBasicoResponse toResponse(CatalogoBasicoView view) {
        return new CatalogoBasicoResponse(view.id(), view.nombre(), view.descripcion(), view.activo());
    }
}
