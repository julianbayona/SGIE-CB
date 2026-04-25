package com.ejemplo.monolitomodular.eventos.presentacion.rest;

import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CrearEventoCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.ConsultarEventoUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.CrearEventoUseCase;
import com.ejemplo.monolitomodular.eventos.presentacion.rest.dto.CrearEventoRequest;
import com.ejemplo.monolitomodular.eventos.presentacion.rest.dto.EventoResponse;
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
@RequestMapping("/api/eventos")
public class EventoController {

    private final CrearEventoUseCase crearEventoUseCase;
    private final ConsultarEventoUseCase consultarEventoUseCase;

    public EventoController(
            CrearEventoUseCase crearEventoUseCase,
            ConsultarEventoUseCase consultarEventoUseCase
    ) {
        this.crearEventoUseCase = crearEventoUseCase;
        this.consultarEventoUseCase = consultarEventoUseCase;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> crear(@Valid @RequestBody CrearEventoRequest request) {
        EventoView evento = crearEventoUseCase.ejecutar(
                new CrearEventoCommand(
                        request.clienteId(),
                        request.tipoEvento(),
                        request.tipoComida(),
                        request.fechaEvento(),
                        request.horaInicio(),
                        request.duracionHoras(),
                        request.numeroPersonas(),
                        request.salonIds(),
                        request.observaciones(),
                        request.usuarioResponsableId()
                )
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(evento.id())
                .toUri();

        return ResponseEntity.created(location).body(toResponse(evento));
    }

    @GetMapping("/{id}")
    public EventoResponse obtenerPorId(@PathVariable UUID id) {
        return toResponse(consultarEventoUseCase.obtenerPorId(id));
    }

    @GetMapping
    public List<EventoResponse> listar() {
        return consultarEventoUseCase.listar().stream()
                .map(this::toResponse)
                .toList();
    }

    private EventoResponse toResponse(EventoView evento) {
        return new EventoResponse(
                evento.id(),
                evento.clienteId(),
                evento.tipoEvento(),
                evento.tipoComida(),
                evento.fechaEvento(),
                evento.horaInicio(),
                evento.horaFin(),
                evento.numeroPersonas(),
                evento.estado(),
                evento.observaciones(),
                evento.salonIds()
        );
    }
}
