package com.ejemplo.monolitomodular.pruebasplato.aplicacion.listener;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.NotificacionView;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoNotificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.evento.PruebaPlatoProgramadaEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrearNotificacionPersonalPruebaPlatoListenerTest {

    @Test
    void deberiaCrearNotificacionParaChefGerenteYTesorero() {
        CrearNotificacionUseCaseStub useCase = new CrearNotificacionUseCaseStub();
        CrearNotificacionPersonalPruebaPlatoListener listener = new CrearNotificacionPersonalPruebaPlatoListener(
                useCase,
                "573001111111",
                "573002222222",
                "573003333333",
                "",
                "",
                ""
        );
        UUID eventoId = UUID.randomUUID();

        listener.manejar(new PruebaPlatoProgramadaEvent(
                UUID.randomUUID(),
                eventoId,
                UUID.randomUUID(),
                "Cliente Uno",
                "573009999999",
                "cliente@test.com",
                LocalDateTime.of(2026, 9, 10, 10, 0)
        ));

        assertEquals(eventoId, useCase.command().eventoId());
        assertEquals(TipoNotificacion.PRUEBA_PLATO_PERSONAL, useCase.command().tipo());
        assertEquals(3, useCase.command().destinatarios().size());
    }

    @Test
    void noDeberiaCrearNotificacionSiFaltaUnDestinatarioFijo() {
        CrearNotificacionUseCaseStub useCase = new CrearNotificacionUseCaseStub();
        CrearNotificacionPersonalPruebaPlatoListener listener = new CrearNotificacionPersonalPruebaPlatoListener(
                useCase,
                "573001111111",
                "",
                "573003333333",
                "",
                "",
                ""
        );

        listener.manejar(new PruebaPlatoProgramadaEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Cliente Uno",
                "573009999999",
                "cliente@test.com",
                LocalDateTime.of(2026, 9, 10, 10, 0)
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
