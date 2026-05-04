package com.ejemplo.monolitomodular.eventos.aplicacion.listener;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.evento.EventoConfirmadoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CrearEventoCalendarEventoConfirmadoListener {

    private final EventoCalendarRepository eventoCalendarRepository;

    public CrearEventoCalendarEventoConfirmadoListener(EventoCalendarRepository eventoCalendarRepository) {
        this.eventoCalendarRepository = eventoCalendarRepository;
    }

    @EventListener
    public void manejar(EventoConfirmadoEvent event) {
        eventoCalendarRepository.guardar(EventoCalendar.pendiente(
                OrigenEventoCalendar.EVENTO,
                event.eventoId(),
                event.eventoId(),
                TipoOperacionCalendar.CREAR,
                payload(event)
        ));
    }

    private String payload(EventoConfirmadoEvent event) {
        return """
                {"resumen":"Evento confirmado Club Boyaca","fechaInicio":"%s","fechaFin":"%s","origen":"EVENTO_CONFIRMADO"}
                """.formatted(event.fechaHoraInicio(), event.fechaHoraFin()).trim();
    }
}
