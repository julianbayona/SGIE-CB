package com.ejemplo.monolitomodular.calendario.aplicacion.servicio;

import com.ejemplo.monolitomodular.calendario.aplicacion.dto.EventoCalendarView;
import com.ejemplo.monolitomodular.calendario.aplicacion.dto.SincronizarGoogleCalendarCommand;
import com.ejemplo.monolitomodular.calendario.aplicacion.dto.SincronizarGoogleCalendarResult;
import com.ejemplo.monolitomodular.calendario.aplicacion.puerto.entrada.ProcesarEventosCalendarPendientesUseCase;
import com.ejemplo.monolitomodular.calendario.aplicacion.puerto.entrada.ReintentarEventoCalendarUseCase;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.GoogleCalendarPort;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventoCalendarApplicationService implements ProcesarEventosCalendarPendientesUseCase, ReintentarEventoCalendarUseCase {

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

    @Override
    @Transactional
    public EventoCalendarView reintentar(java.util.UUID eventoCalendarId) {
        EventoCalendar eventoCalendar = eventoCalendarRepository.buscarPorId(eventoCalendarId)
                .orElseThrow(() -> new DomainException("Evento de calendario no encontrado"));
        return toView(eventoCalendarRepository.guardar(eventoCalendar.reintentar()));
    }

    private EventoCalendarView toView(EventoCalendar eventoCalendar) {
        return new EventoCalendarView(
                eventoCalendar.getId(),
                eventoCalendar.getOrigenTipo(),
                eventoCalendar.getOrigenId(),
                eventoCalendar.getEventoId(),
                eventoCalendar.getTipo(),
                eventoCalendar.getGoogleEventId(),
                eventoCalendar.getFechaSync(),
                eventoCalendar.getEstado(),
                eventoCalendar.getIntentos(),
                eventoCalendar.getMensajeError()
        );
    }
}
