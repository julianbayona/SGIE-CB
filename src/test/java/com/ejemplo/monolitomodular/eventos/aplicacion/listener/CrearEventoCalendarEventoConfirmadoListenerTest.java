package com.ejemplo.monolitomodular.eventos.aplicacion.listener;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.evento.EventoConfirmadoEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrearEventoCalendarEventoConfirmadoListenerTest {

    @Test
    void deberiaCrearEventoCalendarPendienteCuandoEventoSeConfirma() {
        EventoCalendarRepositoryStub repository = new EventoCalendarRepositoryStub();
        CrearEventoCalendarEventoConfirmadoListener listener = new CrearEventoCalendarEventoConfirmadoListener(repository);
        UUID eventoId = UUID.randomUUID();

        listener.manejar(new EventoConfirmadoEvent(
                eventoId,
                UUID.randomUUID(),
                LocalDateTime.of(2026, 9, 10, 8, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0)
        ));

        assertEquals(OrigenEventoCalendar.EVENTO, repository.guardado().getOrigenTipo());
        assertEquals(eventoId, repository.guardado().getOrigenId());
        assertEquals(eventoId, repository.guardado().getEventoId());
        assertEquals(TipoOperacionCalendar.CREAR, repository.guardado().getTipo());
        assertEquals(EstadoEventoCalendar.PENDIENTE, repository.guardado().getEstado());
    }

    private static class EventoCalendarRepositoryStub implements EventoCalendarRepository {

        private EventoCalendar guardado;

        @Override
        public EventoCalendar guardar(EventoCalendar eventoCalendar) {
            this.guardado = eventoCalendar;
            return eventoCalendar;
        }

        @Override
        public List<EventoCalendar> buscarPendientes(int limite) {
            return List.of();
        }

        EventoCalendar guardado() {
            return guardado;
        }
    }
}
