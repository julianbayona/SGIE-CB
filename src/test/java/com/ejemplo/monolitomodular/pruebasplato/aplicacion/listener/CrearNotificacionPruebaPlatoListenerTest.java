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
import static org.junit.jupiter.api.Assertions.assertTrue;

class CrearNotificacionPruebaPlatoListenerTest {

    @Test
    void deberiaCrearNotificacionParaClienteCuandoSeProgramaPruebaPlato() {
        CrearNotificacionUseCaseStub useCase = new CrearNotificacionUseCaseStub();
        CrearNotificacionPruebaPlatoListener listener = new CrearNotificacionPruebaPlatoListener(useCase);
        UUID eventoId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        LocalDateTime fechaRealizacion = LocalDateTime.now().plusDays(3);

        listener.manejar(new PruebaPlatoProgramadaEvent(
                UUID.randomUUID(),
                eventoId,
                clienteId,
                "Cliente Uno",
                "573001112233",
                fechaRealizacion
        ));

        assertEquals(eventoId, useCase.command().eventoId());
        assertEquals(TipoNotificacion.PRUEBA_PLATO_CLIENTE, useCase.command().tipo());
        assertEquals("573001112233", useCase.command().destinatarios().get(0).telefono());
        assertTrue(useCase.command().payloadJson().contains("Cliente Uno"));
        assertTrue(useCase.command().payloadJson().contains(fechaRealizacion.toString()));
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
