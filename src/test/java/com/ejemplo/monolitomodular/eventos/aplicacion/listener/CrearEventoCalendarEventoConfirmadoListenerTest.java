package com.ejemplo.monolitomodular.eventos.aplicacion.listener;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.evento.EventoConfirmadoEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrearEventoCalendarEventoConfirmadoListenerTest {

    @Test
    void deberiaCrearEventoCalendarPendienteCuandoEventoSeConfirma() {
        EventoCalendarRepositoryStub repository = new EventoCalendarRepositoryStub();
        Cliente cliente = Cliente.nuevo("123", "Cliente Uno", "573001112233", "cliente@test.com", TipoCliente.NO_SOCIO, UUID.randomUUID());
        CrearEventoCalendarEventoConfirmadoListener listener = new CrearEventoCalendarEventoConfirmadoListener(
                repository,
                new ClienteRepositoryStub(cliente),
                "gerente@club.com,tesorero@club.com"
        );
        UUID eventoId = UUID.randomUUID();

        listener.manejar(new EventoConfirmadoEvent(
                eventoId,
                cliente.getId(),
                LocalDateTime.of(2026, 9, 10, 8, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0)
        ));

        assertEquals(OrigenEventoCalendar.EVENTO, repository.guardado().getOrigenTipo());
        assertEquals(eventoId, repository.guardado().getOrigenId());
        assertEquals(eventoId, repository.guardado().getEventoId());
        assertEquals(TipoOperacionCalendar.CREAR, repository.guardado().getTipo());
        assertEquals(EstadoEventoCalendar.PENDIENTE, repository.guardado().getEstado());
        assertEquals(true, repository.guardado().getPayloadJson().contains("cliente@test.com"));
        assertEquals(true, repository.guardado().getPayloadJson().contains("gerente@club.com"));
    }

    private static class ClienteRepositoryStub implements ClienteRepository {

        private final Cliente cliente;

        private ClienteRepositoryStub(Cliente cliente) {
            this.cliente = cliente;
        }

        @Override
        public Cliente guardar(Cliente cliente) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Cliente> buscarPorId(UUID id) {
            return cliente.getId().equals(id) ? Optional.of(cliente) : Optional.empty();
        }

        @Override
        public Optional<Cliente> buscarPorCedula(String cedula) {
            return Optional.empty();
        }

        @Override
        public List<Cliente> listar() {
            return List.of(cliente);
        }

        @Override
        public List<Cliente> buscarPorFiltro(String filtro) {
            return List.of(cliente);
        }
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
