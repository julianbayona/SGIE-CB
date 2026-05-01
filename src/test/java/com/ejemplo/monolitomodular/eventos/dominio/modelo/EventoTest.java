package com.ejemplo.monolitomodular.eventos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventoTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 5, 10, 18, 0);
    private static final LocalDateTime FIN = LocalDateTime.of(2026, 5, 10, 22, 0);

    @Test
    void deberiaCrearEventoNuevo() {
        UUID clienteId = UUID.randomUUID();
        UUID tipoEventoId = UUID.randomUUID();
        UUID tipoComidaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        Evento evento = Evento.nuevo(clienteId, tipoEventoId, tipoComidaId, usuarioId, INICIO, FIN);

        assertNotNull(evento.getId());
        assertEquals(clienteId, evento.getClienteId());
        assertEquals(tipoEventoId, evento.getTipoEventoId());
        assertEquals(tipoComidaId, evento.getTipoComidaId());
        assertEquals(usuarioId, evento.getUsuarioCreadorId());
        assertEquals(INICIO, evento.getFechaHoraInicio());
        assertEquals(FIN, evento.getFechaHoraFin());
        assertEquals(EstadoEvento.PENDIENTE, evento.getEstado());
        assertNull(evento.getGcalEventId());
    }

    @Test
    void deberiaReconstruirEvento() {
        UUID id = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID tipoEventoId = UUID.randomUUID();
        UUID tipoComidaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        Evento evento = Evento.reconstruir(id, clienteId, tipoEventoId, tipoComidaId, usuarioId,
                INICIO, FIN, EstadoEvento.CONFIRMADO, "gcal123");

        assertEquals(id, evento.getId());
        assertEquals(EstadoEvento.CONFIRMADO, evento.getEstado());
        assertEquals("gcal123", evento.getGcalEventId());
    }

    @Test
    void noDeberiaCrearEventoConClienteIdNulo() {
        assertThrows(NullPointerException.class, () ->
                Evento.nuevo(null, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), INICIO, FIN)
        );
    }

    @Test
    void noDeberiaCrearEventoConTipoEventoIdNulo() {
        assertThrows(NullPointerException.class, () ->
                Evento.nuevo(UUID.randomUUID(), null, UUID.randomUUID(), UUID.randomUUID(), INICIO, FIN)
        );
    }

    @Test
    void noDeberiaCrearEventoConTipoComidaIdNulo() {
        assertThrows(NullPointerException.class, () ->
                Evento.nuevo(UUID.randomUUID(), UUID.randomUUID(), null, UUID.randomUUID(), INICIO, FIN)
        );
    }

    @Test
    void noDeberiaCrearEventoConUsuarioCreadorNulo() {
        assertThrows(NullPointerException.class, () ->
                Evento.nuevo(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null, INICIO, FIN)
        );
    }

    @Test
    void noDeberiaCrearEventoConFechaInicioNula() {
        assertThrows(NullPointerException.class, () ->
                Evento.nuevo(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null, FIN)
        );
    }

    @Test
    void noDeberiaCrearEventoConFechaFinNula() {
        assertThrows(NullPointerException.class, () ->
                Evento.nuevo(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), INICIO, null)
        );
    }

    @Test
    void noDeberiaCrearEventoConFechaFinIgualAInicio() {
        assertThrows(DomainException.class, () ->
                Evento.nuevo(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), INICIO, INICIO)
        );
    }

    @Test
    void noDeberiaCrearEventoConFechaFinAnteriorAInicio() {
        assertThrows(DomainException.class, () ->
                Evento.nuevo(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), FIN, INICIO)
        );
    }

    @Test
    void deberiaNormalizarGcalEventIdBlankANulo() {
        Evento evento = Evento.reconstruir(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), INICIO, FIN, EstadoEvento.PENDIENTE, "   ");
        assertNull(evento.getGcalEventId());
    }

    @Test
    void noDeberiaAceptarGcalEventIdMayorA255Caracteres() {
        String idLargo = "x".repeat(256);
        assertThrows(DomainException.class, () ->
                Evento.reconstruir(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        UUID.randomUUID(), UUID.randomUUID(), INICIO, FIN, EstadoEvento.PENDIENTE, idLargo)
        );
    }

    @Test
    void deberiaAceptarGcalEventIdDe255Caracteres() {
        String id255 = "x".repeat(255);
        Evento evento = Evento.reconstruir(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), INICIO, FIN, EstadoEvento.PENDIENTE, id255);
        assertEquals(id255, evento.getGcalEventId());
    }
}