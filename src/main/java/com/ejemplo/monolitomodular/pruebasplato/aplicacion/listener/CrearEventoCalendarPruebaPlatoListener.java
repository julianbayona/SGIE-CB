package com.ejemplo.monolitomodular.pruebasplato.aplicacion.listener;

import com.ejemplo.monolitomodular.calendario.aplicacion.dto.GoogleCalendarPayload;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.evento.PruebaPlatoProgramadaEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class CrearEventoCalendarPruebaPlatoListener {

    private final EventoCalendarRepository eventoCalendarRepository;
    private final String correoChef;
    private final String correoGerente;
    private final String correoTesorero;
    private final ObjectMapper objectMapper;

    public CrearEventoCalendarPruebaPlatoListener(
            EventoCalendarRepository eventoCalendarRepository,
            ObjectMapper objectMapper,
            @Value("${sgie.calendario.prueba-plato.chef-correo:}") String correoChef,
            @Value("${sgie.calendario.prueba-plato.gerente-correo:}") String correoGerente,
            @Value("${sgie.calendario.prueba-plato.tesorero-correo:}") String correoTesorero
    ) {
        this.eventoCalendarRepository = eventoCalendarRepository;
        this.objectMapper = objectMapper;
        this.correoChef = correoChef;
        this.correoGerente = correoGerente;
        this.correoTesorero = correoTesorero;
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
        try {
            return objectMapper.writeValueAsString(new GoogleCalendarPayload(
                    "Prueba de plato - " + event.nombreCliente(),
                    event.fechaRealizacion(),
                    event.fechaRealizacion().plusHours(1),
                    "PRUEBA_PLATO",
                    attendees(event)
            ));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("No fue posible construir el payload de Google Calendar", ex);
        }
    }

    private List<GoogleCalendarPayload.Attendee> attendees(PruebaPlatoProgramadaEvent event) {
        return Stream.of(event.correoCliente(), correoChef, correoGerente, correoTesorero)
                .map(String::trim)
                .filter(correo -> !correo.isBlank())
                .map(GoogleCalendarPayload.Attendee::new)
                .toList();
    }
}
