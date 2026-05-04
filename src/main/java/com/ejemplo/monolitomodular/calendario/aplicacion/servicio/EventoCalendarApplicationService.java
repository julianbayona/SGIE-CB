package com.ejemplo.monolitomodular.calendario.aplicacion.servicio;

import com.ejemplo.monolitomodular.calendario.aplicacion.dto.SincronizarGoogleCalendarCommand;
import com.ejemplo.monolitomodular.calendario.aplicacion.dto.SincronizarGoogleCalendarResult;
import com.ejemplo.monolitomodular.calendario.aplicacion.puerto.entrada.ProcesarEventosCalendarPendientesUseCase;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.GoogleCalendarPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventoCalendarApplicationService implements ProcesarEventosCalendarPendientesUseCase {

    private final EventoCalendarRepository eventoCalendarRepository;
    private final GoogleCalendarPort googleCalendarPort;

    public EventoCalendarApplicationService(
            EventoCalendarRepository eventoCalendarRepository,
            GoogleCalendarPort googleCalendarPort
    ) {
        this.eventoCalendarRepository = eventoCalendarRepository;
        this.googleCalendarPort = googleCalendarPort;
    }

    @Override
    @Transactional
    public int procesarPendientes(int limite) {
        return eventoCalendarRepository.buscarPendientes(limite).stream()
                .mapToInt(this::procesar)
                .sum();
    }

    private int procesar(EventoCalendar eventoCalendar) {
        EventoCalendar enIntento = eventoCalendarRepository.guardar(eventoCalendar.iniciarIntento());
        SincronizarGoogleCalendarResult resultado = googleCalendarPort.sincronizar(new SincronizarGoogleCalendarCommand(
                enIntento.getId(),
                enIntento.getTipo(),
                enIntento.getGoogleEventId(),
                enIntento.getPayloadJson()
        ));
        eventoCalendarRepository.guardar(resultado.exitoso()
                ? enIntento.marcarSincronizado(resultado.googleEventId())
                : enIntento.marcarError(resultado.mensaje()));
        return resultado.exitoso() ? 1 : 0;
    }
}
