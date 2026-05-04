package com.ejemplo.monolitomodular.eventos.aplicacion.listener;

import com.ejemplo.monolitomodular.eventos.aplicacion.evento.EventoConfirmadoEvent;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.NotificacionView;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoNotificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrearNotificacionPersonalEventoConfirmadoListenerTest {

    @Test
    void deberiaCrearNotificacionParaPersonalConfiguradoCuandoEventoSeConfirma() {
        CrearNotificacionUseCaseStub useCase = new CrearNotificacionUseCaseStub();
        CrearNotificacionPersonalEventoConfirmadoListener listener = new CrearNotificacionPersonalEventoConfirmadoListener(
                useCase,
                "573001112233,573004445566"
        );
        UUID eventoId = UUID.randomUUID();

        listener.manejar(new EventoConfirmadoEvent(
                eventoId,
                UUID.randomUUID(),
                LocalDateTime.of(2026, 9, 10, 8, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0)
        ));

        assertEquals(eventoId, useCase.command().eventoId());
        assertEquals(TipoNotificacion.EVENTO_CONFIRMADO_PERSONAL, useCase.command().tipo());
        assertEquals(2, useCase.command().destinatarios().size());
    }

    @Test
    void noDeberiaCrearNotificacionSiNoHayTelefonosConfigurados() {
        CrearNotificacionUseCaseStub useCase = new CrearNotificacionUseCaseStub();
        CrearNotificacionPersonalEventoConfirmadoListener listener = new CrearNotificacionPersonalEventoConfirmadoListener(
                useCase,
                ""
        );

        listener.manejar(new EventoConfirmadoEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.of(2026, 9, 10, 8, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0)
        ));

        assertEquals(0, useCase.total());
    }

    private static class CrearNotificacionUseCaseStub implements CrearNotificacionUseCase {

        private CrearNotificacionCommand command;
        private int total;

        @Override
        public NotificacionView ejecutar(CrearNotificacionCommand command) {
            this.command = command;
            this.total++;
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

        int total() {
            return total;
        }
    }
}
