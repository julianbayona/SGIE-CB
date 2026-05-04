package com.ejemplo.monolitomodular.pruebasplato.aplicacion.listener;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.evento.PruebaPlatoProgramadaEvent;
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

    public CrearEventoCalendarPruebaPlatoListener(
            EventoCalendarRepository eventoCalendarRepository,
            @Value("${sgie.calendario.prueba-plato.chef-correo:}") String correoChef,
            @Value("${sgie.calendario.prueba-plato.gerente-correo:}") String correoGerente,
            @Value("${sgie.calendario.prueba-plato.tesorero-correo:}") String correoTesorero
    ) {
        this.eventoCalendarRepository = eventoCalendarRepository;
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
        return """
                {"resumen":"Prueba de plato - %s","fechaInicio":"%s","fechaFin":"%s","origen":"PRUEBA_PLATO","attendees":%s}
                """.formatted(
                event.nombreCliente(),
                event.fechaRealizacion(),
                event.fechaRealizacion().plusHours(1),
                attendeesJson(event)
        ).trim();
    }

    private String attendeesJson(PruebaPlatoProgramadaEvent event) {
        List<String> correos = Stream.of(event.correoCliente(), correoChef, correoGerente, correoTesorero)
                .map(String::trim)
                .filter(correo -> !correo.isBlank())
                .toList();
        return correos.stream()
                .map(correo -> "{\"email\":\"%s\"}".formatted(correo))
                .toList()
                .toString();
    }
}
