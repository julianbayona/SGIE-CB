package com.ejemplo.monolitomodular.eventos.presentacion.rest;

import com.ejemplo.monolitomodular.auth.infraestructura.seguridad.UsuarioAutenticado;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CrearEventoCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CrearReservaSalonCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.ModificarReservaSalonCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.ReservaSalonView;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.ConfirmarEventoUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.ConsultarEventoUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.CrearEventoUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.CrearReservaSalonUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.ModificarReservaSalonUseCase;
import com.ejemplo.monolitomodular.eventos.presentacion.rest.dto.CrearEventoRequest;
import com.ejemplo.monolitomodular.eventos.presentacion.rest.dto.CrearReservaSalonRequest;
import com.ejemplo.monolitomodular.eventos.presentacion.rest.dto.EventoResponse;
import com.ejemplo.monolitomodular.eventos.presentacion.rest.dto.ModificarReservaSalonRequest;
import com.ejemplo.monolitomodular.eventos.presentacion.rest.dto.ReservaSalonResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    private final CrearReservaSalonUseCase crearReservaSalonUseCase;
    private final ModificarReservaSalonUseCase modificarReservaSalonUseCase;
    private final ConfirmarEventoUseCase confirmarEventoUseCase;

    public EventoController(
            CrearEventoUseCase crearEventoUseCase,
            ConsultarEventoUseCase consultarEventoUseCase,
            CrearReservaSalonUseCase crearReservaSalonUseCase,
            ModificarReservaSalonUseCase modificarReservaSalonUseCase,
            ConfirmarEventoUseCase confirmarEventoUseCase
    ) {
        this.crearEventoUseCase = crearEventoUseCase;
        this.consultarEventoUseCase = consultarEventoUseCase;
        this.crearReservaSalonUseCase = crearReservaSalonUseCase;
        this.modificarReservaSalonUseCase = modificarReservaSalonUseCase;
        this.confirmarEventoUseCase = confirmarEventoUseCase;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> crear(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @Valid @RequestBody CrearEventoRequest request
    ) {
        EventoView evento = crearEventoUseCase.ejecutar(
                new CrearEventoCommand(
                        request.clienteId(),
                        request.tipoEventoId(),
                        request.tipoComidaId(),
                        usuario.id(),
                        request.fechaHoraInicio(),
                        request.fechaHoraFin()
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

    @PostMapping("/{eventoId}/reservas")
    public EventoResponse crearReserva(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable UUID eventoId,
            @Valid @RequestBody CrearReservaSalonRequest request
    ) {
        return toResponse(crearReservaSalonUseCase.ejecutar(
                eventoId,
                new CrearReservaSalonCommand(
                        usuario.id(),
                        request.salonId(),
                        request.numInvitados(),
                        request.fechaHoraInicio(),
                        request.fechaHoraFin()
                )
        ));
    }

    @PatchMapping("/reservas/{reservaRaizId}")
    public EventoResponse modificarReserva(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable UUID reservaRaizId,
            @Valid @RequestBody ModificarReservaSalonRequest request
    ) {
        return toResponse(modificarReservaSalonUseCase.ejecutar(
                reservaRaizId,
                new ModificarReservaSalonCommand(
                        usuario.id(),
                        request.salonId(),
                        request.numInvitados(),
                        request.fechaHoraInicio(),
                        request.fechaHoraFin()
                )
        ));
    }

    @GetMapping
    public List<EventoResponse> listar() {
        return consultarEventoUseCase.listar().stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping("/{eventoId}/confirmar")
    public EventoResponse confirmar(
            @AuthenticationPrincipal UsuarioAutenticado usuario,
            @PathVariable UUID eventoId
    ) {
        return toResponse(confirmarEventoUseCase.confirmar(eventoId, usuario.id()));
    }

    private EventoResponse toResponse(EventoView evento) {
        return new EventoResponse(
                evento.id(),
                evento.clienteId(),
                evento.tipoEventoId(),
                evento.tipoComidaId(),
                evento.usuarioCreadorId(),
                evento.estado(),
                evento.gcalEventId(),
                evento.fechaHoraInicio(),
                evento.fechaHoraFin(),
                evento.reservas().stream().map(this::toReservaResponse).toList()
        );
    }

    private ReservaSalonResponse toReservaResponse(ReservaSalonView reserva) {
        return new ReservaSalonResponse(
                reserva.id(),
                reserva.reservaRaizId(),
                reserva.salonId(),
                reserva.numInvitados(),
                reserva.fechaHoraInicio(),
                reserva.fechaHoraFin(),
                reserva.version(),
                reserva.vigente()
        );
    }
}
