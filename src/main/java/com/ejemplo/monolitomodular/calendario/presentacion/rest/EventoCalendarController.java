package com.ejemplo.monolitomodular.calendario.presentacion.rest;

import com.ejemplo.monolitomodular.calendario.aplicacion.dto.EventoCalendarView;
import com.ejemplo.monolitomodular.calendario.aplicacion.puerto.entrada.ReintentarEventoCalendarUseCase;
import com.ejemplo.monolitomodular.calendario.presentacion.rest.dto.EventoCalendarResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/calendario/eventos")
public class EventoCalendarController {

    private final ReintentarEventoCalendarUseCase reintentarEventoCalendarUseCase;

    public EventoCalendarController(ReintentarEventoCalendarUseCase reintentarEventoCalendarUseCase) {
        this.reintentarEventoCalendarUseCase = reintentarEventoCalendarUseCase;
    }

    @PostMapping("/{eventoCalendarId}/reintentar")
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
