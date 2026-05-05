package com.ejemplo.monolitomodular.menus.presentacion.rest;

import com.ejemplo.monolitomodular.menus.aplicacion.dto.TipoMomentoMenuCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.TipoMomentoMenuView;
import com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada.GestionarTipoMomentoMenuUseCase;
import com.ejemplo.monolitomodular.menus.presentacion.rest.dto.TipoMomentoMenuRequest;
import com.ejemplo.monolitomodular.menus.presentacion.rest.dto.TipoMomentoMenuResponse;
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
@RequestMapping("/api/catalogos/tipos-momento-menu")
public class TipoMomentoMenuController {

    private final GestionarTipoMomentoMenuUseCase useCase;

    public TipoMomentoMenuController(GestionarTipoMomentoMenuUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<TipoMomentoMenuResponse> crear(@Valid @RequestBody TipoMomentoMenuRequest request) {
        TipoMomentoMenuView tipoMomento = useCase.crear(new TipoMomentoMenuCommand(request.nombre()));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tipoMomento.id())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(tipoMomento));
    }

    @PutMapping("/{id}")
    public TipoMomentoMenuResponse actualizar(@PathVariable UUID id, @Valid @RequestBody TipoMomentoMenuRequest request) {
        return toResponse(useCase.actualizar(id, new TipoMomentoMenuCommand(request.nombre())));
    }

    @DeleteMapping("/{id}")
    public TipoMomentoMenuResponse desactivar(@PathVariable UUID id) {
        return toResponse(useCase.desactivar(id));
    }

    @GetMapping("/{id}")
    public TipoMomentoMenuResponse obtener(@PathVariable UUID id) {
        return toResponse(useCase.obtener(id));
    }

    @GetMapping
    public List<TipoMomentoMenuResponse> listar() {
        return useCase.listar().stream().map(this::toResponse).toList();
    }

    private TipoMomentoMenuResponse toResponse(TipoMomentoMenuView view) {
        return new TipoMomentoMenuResponse(view.id(), view.nombre(), view.activo());
    }
}
