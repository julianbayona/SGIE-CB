package com.ejemplo.monolitomodular.eventos.aplicacion.listener;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.evento.EventoConfirmadoEvent;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.NotificacionView;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoNotificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrearNotificacionClienteEventoConfirmadoListenerTest {

    @Test
    void deberiaCrearNotificacionParaClienteCuandoEventoSeConfirma() {
        Cliente cliente = Cliente.nuevo("123", "Cliente Uno", "573001112233", "cliente@test.com", TipoCliente.NO_SOCIO, UUID.randomUUID());
        CrearNotificacionUseCaseStub useCase = new CrearNotificacionUseCaseStub();
        CrearNotificacionClienteEventoConfirmadoListener listener = new CrearNotificacionClienteEventoConfirmadoListener(
                new ClienteRepositoryStub(cliente),
                useCase
        );
        UUID eventoId = UUID.randomUUID();

        listener.manejar(new EventoConfirmadoEvent(
                eventoId,
                cliente.getId(),
                LocalDateTime.of(2026, 9, 10, 8, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0)
        ));

        assertEquals(eventoId, useCase.command().eventoId());
        assertEquals(TipoNotificacion.EVENTO_CONFIRMADO_CLIENTE, useCase.command().tipo());
        assertEquals(cliente.getTelefono(), useCase.command().destinatarios().get(0).telefono());
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

    private static class CrearNotificacionUseCaseStub implements CrearNotificacionUseCase {

        private CrearNotificacionCommand command;

        @Override
        public NotificacionView ejecutar(CrearNotificacionCommand command) {
            this.command = command;
            return new NotificacionView(
                    UUID.randomUUID(),
                    command.eventoId(),
                    command.tipo(),
                    command.fechaProgramada(),
                    null,
                    EstadoNotificacion.PENDIENTE,
                    0,
                    command.payloadJson()
            );
        }

        CrearNotificacionCommand command() {
            return command;
        }
    }
}
