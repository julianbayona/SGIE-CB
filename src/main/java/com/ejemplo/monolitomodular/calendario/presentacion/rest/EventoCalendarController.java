package com.ejemplo.monolitomodular.calendario.presentacion.rest;

import com.ejemplo.monolitomodular.calendario.aplicacion.dto.EventoCalendarView;
import com.ejemplo.monolitomodular.calendario.aplicacion.puerto.entrada.ListarEventosCalendarPorEventoUseCase;
import com.ejemplo.monolitomodular.calendario.aplicacion.puerto.entrada.ReintentarEventoCalendarUseCase;
import com.ejemplo.monolitomodular.calendario.presentacion.rest.dto.EventoCalendarResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/calendario/eventos")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'TESORERO')")
public class EventoCalendarController {

    private final ReintentarEventoCalendarUseCase reintentarEventoCalendarUseCase;
    private final ListarEventosCalendarPorEventoUseCase listarEventosCalendarPorEventoUseCase;

    public EventoCalendarController(
            ReintentarEventoCalendarUseCase reintentarEventoCalendarUseCase,
            ListarEventosCalendarPorEventoUseCase listarEventosCalendarPorEventoUseCase
    ) {
        this.reintentarEventoCalendarUseCase = reintentarEventoCalendarUseCase;
        this.listarEventosCalendarPorEventoUseCase = listarEventosCalendarPorEventoUseCase;
    }

    @GetMapping("/evento/{eventoId}")
    public List<EventoCalendarResponse> listarPorEvento(@PathVariable UUID eventoId) {
        return listarEventosCalendarPorEventoUseCase.listarPorEvento(eventoId).stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping("/{eventoCalendarId}/reintentar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public EventoCalendarResponse reintentar(@PathVariable UUID eventoCalendarId) {
        return toResponse(reintentarEventoCalendarUseCase.reintentar(eventoCalendarId));
    }

    private EventoCalendarResponse toResponse(EventoCalendarView view) {
        return new EventoCalendarResponse(
                view.id(),
                view.origenTipo(),
                view.origenId(),
                view.eventoId(),
                view.tipo(),
                view.googleEventId(),
                view.fechaSync(),
                view.estado(),
                view.intentos(),
                view.mensajeError()
        );
    }

}
