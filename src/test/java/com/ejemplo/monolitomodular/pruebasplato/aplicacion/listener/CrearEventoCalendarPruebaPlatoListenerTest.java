package com.ejemplo.monolitomodular.pruebasplato.aplicacion.listener;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.evento.PruebaPlatoProgramadaEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrearEventoCalendarPruebaPlatoListenerTest {

    @Test
    void deberiaCrearEventoCalendarPendienteCuandoSeProgramaPruebaPlato() {
        EventoCalendarRepositoryStub repository = new EventoCalendarRepositoryStub();
        CrearEventoCalendarPruebaPlatoListener listener = new CrearEventoCalendarPruebaPlatoListener(repository);
        UUID pruebaPlatoId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();

        listener.manejar(new PruebaPlatoProgramadaEvent(
                pruebaPlatoId,
                eventoId,
                UUID.randomUUID(),
                "Cliente Uno",
                "573001112233",
                LocalDateTime.now().plusDays(2)
        ));

        assertEquals(OrigenEventoCalendar.PRUEBA_PLATO, repository.guardado().getOrigenTipo());
        assertEquals(pruebaPlatoId, repository.guardado().getOrigenId());
        assertEquals(eventoId, repository.guardado().getEventoId());
        assertEquals(TipoOperacionCalendar.CREAR, repository.guardado().getTipo());
        assertEquals(EstadoEventoCalendar.PENDIENTE, repository.guardado().getEstado());
        assertEquals(0, repository.guardado().getIntentos());
    }

    private static class EventoCalendarRepositoryStub implements EventoCalendarRepository {

        private EventoCalendar guardado;

        @Override
        public EventoCalendar guardar(EventoCalendar eventoCalendar) {
            this.guardado = eventoCalendar;
            return eventoCalendar;
        }

        EventoCalendar guardado() {
            return guardado;
        }
    }
}
