package com.ejemplo.monolitomodular.salones.presentacion.rest;

import com.ejemplo.monolitomodular.salones.aplicacion.dto.ConsultarDisponibilidadSalonesQuery;
import com.ejemplo.monolitomodular.salones.aplicacion.dto.RegistrarSalonCommand;
import com.ejemplo.monolitomodular.salones.aplicacion.dto.SalonView;
import com.ejemplo.monolitomodular.salones.aplicacion.puerto.entrada.ConsultarSalonUseCase;
import com.ejemplo.monolitomodular.salones.aplicacion.puerto.entrada.RegistrarSalonUseCase;
import com.ejemplo.monolitomodular.salones.presentacion.rest.dto.RegistrarSalonRequest;
import com.ejemplo.monolitomodular.salones.presentacion.rest.dto.SalonResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
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
    @PreAuthorize("hasRole('ADMINISTRADOR')")
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

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public SalonResponse actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody RegistrarSalonRequest request
    ) {
        return toResponse(registrarSalonUseCase.actualizar(
                id,
                new RegistrarSalonCommand(request.nombre(), request.capacidad(), request.descripcion())
        ));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public SalonResponse activar(@PathVariable UUID id) {
        return toResponse(registrarSalonUseCase.activar(id));
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public SalonResponse desactivar(@PathVariable UUID id) {
        return toResponse(registrarSalonUseCase.desactivar(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'TESORERO')")
    public SalonResponse obtenerPorId(@PathVariable UUID id) {
        return toResponse(consultarSalonUseCase.obtenerPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'TESORERO')")
    public List<SalonResponse> listar() {
        return consultarSalonUseCase.listar().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/disponibilidad")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'TESORERO')")
    public List<SalonResponse> consultarDisponibilidad(
            @RequestParam LocalDateTime fechaHoraInicio,
            @RequestParam LocalDateTime fechaHoraFin,
            @RequestParam(required = false) Integer capacidadMinima
    ) {
        return consultarSalonUseCase.consultarDisponibilidad(
                        new ConsultarDisponibilidadSalonesQuery(
                                fechaHoraInicio,
                                fechaHoraFin,
                                capacidadMinima
                        )
                ).stream()
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
