package com.ejemplo.monolitomodular.calendario.aplicacion.servicio;

import com.ejemplo.monolitomodular.calendario.aplicacion.dto.SincronizarGoogleCalendarCommand;
import com.ejemplo.monolitomodular.calendario.aplicacion.dto.SincronizarGoogleCalendarResult;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.GoogleCalendarPort;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventoCalendarApplicationServiceTest {

    @Test
    void deberiaProcesarEventoCalendarPendienteYMarcarloSincronizado() {
        EventoCalendarRepositoryStub repository = new EventoCalendarRepositoryStub();
        GoogleCalendarPortStub googleCalendarPort = new GoogleCalendarPortStub(true);
        EventoCalendarApplicationService service = new EventoCalendarApplicationService(repository, googleCalendarPort);
        repository.guardar(eventoPendiente());

        int procesados = service.procesarPendientes(10);

        assertEquals(1, procesados);
        assertEquals(1, googleCalendarPort.envios());
        assertEquals(EstadoEventoCalendar.SINCRONIZADO, repository.ultimo().getEstado());
        assertEquals(1, repository.ultimo().getIntentos());
        assertEquals("google-123", repository.ultimo().getGoogleEventId());
    }

    @Test
    void deberiaMarcarErrorCuandoGoogleCalendarFalla() {
        EventoCalendarRepositoryStub repository = new EventoCalendarRepositoryStub();
        GoogleCalendarPortStub googleCalendarPort = new GoogleCalendarPortStub(false);
        EventoCalendarApplicationService service = new EventoCalendarApplicationService(repository, googleCalendarPort);
        repository.guardar(eventoPendiente());

        int procesados = service.procesarPendientes(10);

        assertEquals(0, procesados);
        assertEquals(1, googleCalendarPort.envios());
        assertEquals(EstadoEventoCalendar.ERROR, repository.ultimo().getEstado());
        assertEquals(1, repository.ultimo().getIntentos());
        assertEquals("Fallo simulado", repository.ultimo().getMensajeError());
    }

    @Test
    void deberiaReintentarEventoCalendarConError() {
        EventoCalendarRepositoryStub repository = new EventoCalendarRepositoryStub();
        EventoCalendarApplicationService service = new EventoCalendarApplicationService(
                repository,
                new GoogleCalendarPortStub(false)
        );
        EventoCalendar conError = repository.guardar(eventoPendiente().iniciarIntento().marcarError("Fallo previo"));

        service.reintentar(conError.getId());

        assertEquals(EstadoEventoCalendar.PENDIENTE, repository.ultimo().getEstado());
        assertEquals(0, repository.ultimo().getIntentos());
        assertEquals(null, repository.ultimo().getMensajeError());
    }

    private static EventoCalendar eventoPendiente() {
        return EventoCalendar.pendiente(
                OrigenEventoCalendar.PRUEBA_PLATO,
                UUID.randomUUID(),
                UUID.randomUUID(),
                TipoOperacionCalendar.CREAR,
                "{\"resumen\":\"Prueba de plato\"}"
        );
    }

    private static class EventoCalendarRepositoryStub implements EventoCalendarRepository {

        private final List<EventoCalendar> eventos = new ArrayList<>();

        @Override
        public EventoCalendar guardar(EventoCalendar eventoCalendar) {
            eventos.removeIf(actual -> actual.getId().equals(eventoCalendar.getId()));
            eventos.add(eventoCalendar);
            return eventoCalendar;
        }

        @Override
        public Optional<EventoCalendar> buscarPorId(UUID id) {
            return eventos.stream()
                    .filter(evento -> evento.getId().equals(id))
                    .findFirst();
        }

        @Override
        public List<EventoCalendar> buscarPendientes(int limite) {
            return eventos.stream()
                    .filter(evento -> evento.getIntentos() < 3)
                    .filter(evento -> evento.getEstado() == EstadoEventoCalendar.PENDIENTE
                            || evento.getEstado() == EstadoEventoCalendar.ERROR)
                    .sorted(Comparator.comparing(evento -> evento.getId().toString()))
                    .limit(limite)
                    .toList();
        }

        EventoCalendar ultimo() {
            return eventos.get(eventos.size() - 1);
        }
    }

    private static class GoogleCalendarPortStub implements GoogleCalendarPort {

        private final boolean exitoso;
        private int envios;

        private GoogleCalendarPortStub(boolean exitoso) {
            this.exitoso = exitoso;
        }

        @Override
        public SincronizarGoogleCalendarResult sincronizar(SincronizarGoogleCalendarCommand command) {
            envios++;
            return exitoso
                    ? SincronizarGoogleCalendarResult.ok("google-123")
                    : SincronizarGoogleCalendarResult.error("Fallo simulado");
        }

        int envios() {
            return envios;
        }
    }
}
