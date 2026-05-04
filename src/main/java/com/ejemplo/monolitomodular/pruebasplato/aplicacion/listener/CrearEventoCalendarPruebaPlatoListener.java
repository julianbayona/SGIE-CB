package com.ejemplo.monolitomodular.pruebasplato.aplicacion.listener;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.evento.PruebaPlatoProgramadaEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CrearEventoCalendarPruebaPlatoListener {

    private final EventoCalendarRepository eventoCalendarRepository;

    public CrearEventoCalendarPruebaPlatoListener(EventoCalendarRepository eventoCalendarRepository) {
        this.eventoCalendarRepository = eventoCalendarRepository;
    }

    @EventListener
    public void manejar(PruebaPlatoProgramadaEvent event) {
        eventoCalendarRepository.guardar(EventoCalendar.pendiente(
                OrigenEventoCalendar.PRUEBA_PLATO,
                event.pruebaPlatoId(),
                event.eventoId(),
                TipoOperacionCalendar.CREAR,
                payload(event)
        ));
    }

    private String payload(PruebaPlatoProgramadaEvent event) {
        return """
                {"resumen":"Prueba de plato - %s","fechaInicio":"%s","fechaFin":"%s","origen":"PRUEBA_PLATO"}
                """.formatted(
                event.nombreCliente(),
                event.fechaRealizacion(),
                event.fechaRealizacion().plusHours(1)
        ).trim();
    }
}
