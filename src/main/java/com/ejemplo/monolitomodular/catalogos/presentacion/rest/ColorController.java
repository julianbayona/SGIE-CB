package com.ejemplo.monolitomodular.catalogos.presentacion.rest;

import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.ColorCommand;
import com.ejemplo.monolitomodular.catalogos.aplicacion.dto.ColorView;
import com.ejemplo.monolitomodular.catalogos.aplicacion.puerto.entrada.GestionarColorUseCase;
import com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto.ColorRequest;
import com.ejemplo.monolitomodular.catalogos.presentacion.rest.dto.ColorResponse;
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
@RequestMapping("/api/catalogos/colores")
public class ColorController {

    private final GestionarColorUseCase gestionarColorUseCase;

    public ColorController(GestionarColorUseCase gestionarColorUseCase) {
        this.gestionarColorUseCase = gestionarColorUseCase;
    }

    @PostMapping
    public ResponseEntity<ColorResponse> crear(@Valid @RequestBody ColorRequest request) {
        ColorView color = gestionarColorUseCase.crearColor(toCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(color.id())
                .toUri();
        return ResponseEntity.created(location).body(toResponse(color));
    }

    @PutMapping("/{id}")
    public ColorResponse actualizar(@PathVariable UUID id, @Valid @RequestBody ColorRequest request) {
        return toResponse(gestionarColorUseCase.actualizarColor(id, toCommand(request)));
    }

    @DeleteMapping("/{id}")
    public ColorResponse desactivar(@PathVariable UUID id) {
        return toResponse(gestionarColorUseCase.desactivarColor(id));
    }

    @GetMapping("/{id}")
    public ColorResponse obtenerPorId(@PathVariable UUID id) {
        return toResponse(gestionarColorUseCase.obtenerColor(id));
    }

    @GetMapping
    public List<ColorResponse> listar() {
        return gestionarColorUseCase.listarColores().stream().map(this::toResponse).toList();
    }

    private ColorCommand toCommand(ColorRequest request) {
        return new ColorCommand(request.nombre(), request.codigoHex());
    }

    private ColorResponse toResponse(ColorView view) {
        return new ColorResponse(view.id(), view.nombre(), view.codigoHex(), view.activo());
    }
}
