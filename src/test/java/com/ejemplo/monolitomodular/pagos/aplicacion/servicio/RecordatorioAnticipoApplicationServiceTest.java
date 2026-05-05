package com.ejemplo.monolitomodular.pagos.aplicacion.servicio;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.NotificacionView;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoNotificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.Notificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.NotificacionRepository;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.Anticipo;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.EventoAnticipoPendiente;
import com.ejemplo.monolitomodular.pagos.dominio.puerto.salida.AnticipoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecordatorioAnticipoApplicationServiceTest {

    @Test
    void deberiaCrearRecordatorioParaEventoConSaldoPendiente() {
        EventoAnticipoPendiente candidato = candidato();
        AnticipoRepositoryStub anticipoRepository = new AnticipoRepositoryStub(List.of(candidato));
        NotificacionRepositoryStub notificacionRepository = new NotificacionRepositoryStub(false);
        CrearNotificacionUseCaseStub crearNotificacionUseCase = new CrearNotificacionUseCaseStub();
        RecordatorioAnticipoApplicationService service = new RecordatorioAnticipoApplicationService(
                anticipoRepository,
                notificacionRepository,
                crearNotificacionUseCase,
                new ObjectMapper()
        );

        int creados = service.procesar(7, 24, 20);

        assertEquals(1, creados);
        assertEquals(1, crearNotificacionUseCase.total());
        assertEquals(TipoNotificacion.RECORDATORIO_ANTICIPO, crearNotificacionUseCase.command().tipo());
        assertEquals(candidato.eventoId(), crearNotificacionUseCase.command().eventoId());
        assertEquals("573001112233", crearNotificacionUseCase.command().destinatarios().get(0).telefono());
        assertEquals("cliente@correo.com", crearNotificacionUseCase.command().destinatarios().get(0).correo());
    }

    @Test
    void noDeberiaDuplicarRecordatorioSiExisteUnoReciente() {
        AnticipoRepositoryStub anticipoRepository = new AnticipoRepositoryStub(List.of(candidato()));
        NotificacionRepositoryStub notificacionRepository = new NotificacionRepositoryStub(true);
        CrearNotificacionUseCaseStub crearNotificacionUseCase = new CrearNotificacionUseCaseStub();
        RecordatorioAnticipoApplicationService service = new RecordatorioAnticipoApplicationService(
                anticipoRepository,
                notificacionRepository,
                crearNotificacionUseCase,
                new ObjectMapper()
        );

        int creados = service.procesar(7, 24, 20);

        assertEquals(0, creados);
        assertEquals(0, crearNotificacionUseCase.total());
    }

    private static EventoAnticipoPendiente candidato() {
        return new EventoAnticipoPendiente(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Cliente Prueba",
                "573001112233",
                "cliente@correo.com",
                LocalDateTime.now().plusDays(3),
                new BigDecimal("2000000.00"),
                new BigDecimal("500000.00")
        );
    }

    private static class AnticipoRepositoryStub implements AnticipoRepository {

        private final List<EventoAnticipoPendiente> candidatos;

        private AnticipoRepositoryStub(List<EventoAnticipoPendiente> candidatos) {
            this.candidatos = candidatos;
        }

        @Override
        public Anticipo guardar(Anticipo anticipo) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Anticipo> listarPorCotizacionId(UUID cotizacionId) {
            return List.of();
        }

        @Override
        public BigDecimal totalPorCotizacionId(UUID cotizacionId) {
            return BigDecimal.ZERO;
        }

        @Override
        public BigDecimal totalPorEventoId(UUID eventoId) {
            return BigDecimal.ZERO;
        }

        @Override
        public List<EventoAnticipoPendiente> buscarEventosConAnticipoPendiente(LocalDateTime desde, LocalDateTime hasta, int limite) {
            return candidatos.stream().limit(limite).toList();
        }
    }

    private static class NotificacionRepositoryStub implements NotificacionRepository {

        private final boolean existeReciente;

        private NotificacionRepositoryStub(boolean existeReciente) {
            this.existeReciente = existeReciente;
        }

        @Override
        public Notificacion guardar(Notificacion notificacion) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Notificacion> buscarPendientes(LocalDateTime fechaReferencia, int limite) {
            return List.of();
        }

        @Override
        public boolean existePorEventoYTipoDesde(UUID eventoId, TipoNotificacion tipo, LocalDateTime fechaDesde) {
            return existeReciente;
        }
    }

    private static class CrearNotificacionUseCaseStub implements CrearNotificacionUseCase {

        private final List<CrearNotificacionCommand> comandos = new ArrayList<>();

        @Override
        public NotificacionView ejecutar(CrearNotificacionCommand command) {
            comandos.add(command);
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
            return comandos.get(comandos.size() - 1);
        }

        int total() {
            return comandos.size();
        }
    }
}
