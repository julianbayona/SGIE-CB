package com.ejemplo.monolitomodular.pruebasplato.aplicacion.listener;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.evento.PruebaPlatoProgramadaEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrearEventoCalendarPruebaPlatoListenerTest {

    @Test
    void deberiaCrearEventoCalendarPendienteCuandoSeProgramaPruebaPlato() {
        EventoCalendarRepositoryStub repository = new EventoCalendarRepositoryStub();
        CrearEventoCalendarPruebaPlatoListener listener = new CrearEventoCalendarPruebaPlatoListener(
                repository,
                objectMapper(),
                "chef@club.com",
                "gerente@club.com",
                "tesorero@club.com"
        );
        UUID pruebaPlatoId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();

        listener.manejar(new PruebaPlatoProgramadaEvent(
                pruebaPlatoId,
                eventoId,
                UUID.randomUUID(),
                "Cliente Uno",
                "573001112233",
                "cliente@test.com",
                LocalDateTime.now().plusDays(2)
        ));

        assertEquals(OrigenEventoCalendar.PRUEBA_PLATO, repository.guardado().getOrigenTipo());
        assertEquals(pruebaPlatoId, repository.guardado().getOrigenId());
        assertEquals(eventoId, repository.guardado().getEventoId());
        assertEquals(TipoOperacionCalendar.CREAR, repository.guardado().getTipo());
        assertEquals(EstadoEventoCalendar.PENDIENTE, repository.guardado().getEstado());
        assertEquals(0, repository.guardado().getIntentos());
        assertEquals(true, repository.guardado().getPayloadJson().contains("cliente@test.com"));
        assertEquals(true, repository.guardado().getPayloadJson().contains("chef@club.com"));
    }

    private static ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    private static class EventoCalendarRepositoryStub implements EventoCalendarRepository {

        private EventoCalendar guardado;

        @Override
        public EventoCalendar guardar(EventoCalendar eventoCalendar) {
            this.guardado = eventoCalendar;
            return eventoCalendar;
        }

        @Override
        public Optional<EventoCalendar> buscarPorId(UUID id) {
            return Optional.empty();
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
