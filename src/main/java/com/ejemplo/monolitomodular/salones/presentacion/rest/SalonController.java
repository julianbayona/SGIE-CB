package com.ejemplo.monolitomodular.salones.presentacion.rest;

import com.ejemplo.monolitomodular.salones.aplicacion.dto.RegistrarSalonCommand;
import com.ejemplo.monolitomodular.salones.aplicacion.dto.SalonView;
import com.ejemplo.monolitomodular.salones.aplicacion.puerto.entrada.ConsultarSalonUseCase;
import com.ejemplo.monolitomodular.salones.aplicacion.puerto.entrada.RegistrarSalonUseCase;
import com.ejemplo.monolitomodular.salones.presentacion.rest.dto.RegistrarSalonRequest;
import com.ejemplo.monolitomodular.salones.presentacion.rest.dto.SalonResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salones")
public class SalonController {

    private final RegistrarSalonUseCase registrarSalonUseCase;
    private final ConsultarSalonUseCase consultarSalonUseCase;

    public SalonController(
            RegistrarSalonUseCase registrarSalonUseCase,
            ConsultarSalonUseCase consultarSalonUseCase
    ) {
        this.registrarSalonUseCase = registrarSalonUseCase;
        this.consultarSalonUseCase = consultarSalonUseCase;
    }

    @PostMapping
    public ResponseEntity<SalonResponse> crear(@Valid @RequestBody RegistrarSalonRequest request) {
        SalonView salon = registrarSalonUseCase.ejecutar(
                new RegistrarSalonCommand(request.nombre(), request.capacidad(), request.descripcion())
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salon.id())
                .toUri();

        return ResponseEntity.created(location).body(toResponse(salon));
    }

    @GetMapping("/{id}")
    public SalonResponse obtenerPorId(@PathVariable UUID id) {
        return toResponse(consultarSalonUseCase.obtenerPorId(id));
    }

    @GetMapping
    public List<SalonResponse> listar() {
        return consultarSalonUseCase.listar().stream()
                .map(this::toResponse)
                .toList();
    }

    private SalonResponse toResponse(SalonView salon) {
        return new SalonResponse(
                salon.id(),
                salon.nombre(),
                salon.capacidad(),
                salon.descripcion(),
                salon.activo()
        );
    }
}
