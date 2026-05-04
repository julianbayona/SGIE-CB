package com.ejemplo.monolitomodular.calendario.infraestructura.google;

import com.ejemplo.monolitomodular.calendario.aplicacion.dto.SincronizarGoogleCalendarCommand;
import com.ejemplo.monolitomodular.calendario.aplicacion.dto.SincronizarGoogleCalendarResult;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.GoogleCalendarPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GoogleCalendarLogAdapter implements GoogleCalendarPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleCalendarLogAdapter.class);

    @Override
    public SincronizarGoogleCalendarResult sincronizar(SincronizarGoogleCalendarCommand command) {
        LOGGER.info(
                "Simulando sincronizacion Google Calendar. eventoCalendarId={}, tipo={}, googleEventId={}, payload={}",
                command.eventoCalendarId(),
                command.tipo(),
                command.googleEventId(),
                command.payloadJson()
        );
        String googleEventId = command.tipo() == TipoOperacionCalendar.CREAR
                ? "mock-gcal-" + command.eventoCalendarId()
                : command.googleEventId();
        return SincronizarGoogleCalendarResult.ok(googleEventId);
    }
}
