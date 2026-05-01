package com.ejemplo.monolitomodular.eventos.dominio.modelo;

import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReservaSalonTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 5, 10, 18, 0);
    private static final LocalDateTime FIN = LocalDateTime.of(2026, 5, 10, 22, 0);

    @Test
    void deberiaCrearReservaNueva() {
        UUID eventoId = UUID.randomUUID();
        UUID salonId = UUID.randomUUID();
        UUID creadoPor = UUID.randomUUID();

        ReservaSalon reserva = ReservaSalon.nueva(eventoId, salonId, 50, INICIO, FIN, creadoPor);

        assertNotNull(reserva.getId());
        assertEquals(reserva.getId(), reserva.getReservaRaizId());
        assertEquals(eventoId, reserva.getEventoId());
        assertEquals(salonId, reserva.getSalonId());
        assertEquals(50, reserva.getNumInvitados());
        assertEquals(INICIO, reserva.getFechaHoraInicio());
        assertEquals(FIN, reserva.getFechaHoraFin());
        assertEquals(1, reserva.getVersion());
        assertTrue(reserva.isVigente());
        assertEquals(creadoPor, reserva.getCreadoPor());
    }

    @Test
    void deberiaCrearNuevaVersionDeReserva() {
        UUID creadoPor = UUID.randomUUID();
        ReservaSalon original = ReservaSalon.nueva(UUID.randomUUID(), UUID.randomUUID(), 50, INICIO, FIN, creadoPor);

        UUID nuevoSalonId = UUID.randomUUID();
        UUID nuevoCreadoPor = UUID.randomUUID();
        LocalDateTime nuevoInicio = LocalDateTime.of(2026, 5, 10, 20, 0);
        LocalDateTime nuevoFin = LocalDateTime.of(2026, 5, 11, 0, 0);

        ReservaSalon nueva = original.crearNuevaVersion(nuevoSalonId, 80, nuevoInicio, nuevoFin, nuevoCreadoPor);

        assertNotEquals(original.getId(), nueva.getId());
        assertEquals(original.getReservaRaizId(), nueva.getReservaRaizId());
        assertEquals(original.getEventoId(), nueva.getEventoId());
        assertEquals(nuevoSalonId, nueva.getSalonId());
        assertEquals(80, nueva.getNumInvitados());
        assertEquals(2, nueva.getVersion());
        assertTrue(nueva.isVigente());
    }

    @Test
    void deberiaMarcarReservaComoNoVigente() {
        ReservaSalon reserva = ReservaSalon.nueva(UUID.randomUUID(), UUID.randomUUID(), 50, INICIO, FIN, UUID.randomUUID());
        ReservaSalon noVigente = reserva.marcarComoNoVigente();

        assertTrue(reserva.isVigente());
        assertFalse(noVigente.isVigente());
        assertEquals(reserva.getId(), noVigente.getId());
        assertEquals(reserva.getVersion(), noVigente.getVersion());
    }

    @Test
    void deberiaReconstruirReservaExistente() {
        UUID id = UUID.randomUUID();
        UUID raizId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();
        UUID salonId = UUID.randomUUID();
        UUID creadoPor = UUID.randomUUID();

        ReservaSalon reserva = ReservaSalon.reconstruir(id, raizId, eventoId, salonId, 100,
                INICIO, FIN, 3, false, creadoPor);

        assertEquals(id, reserva.getId());
        assertEquals(raizId, reserva.getReservaRaizId());
        assertEquals(3, reserva.getVersion());
        assertFalse(reserva.isVigente());
    }

    @Test
    void noDeberiaCrearReservaConNumInvitadosCero() {
        assertThrows(DomainException.class, () ->
                ReservaSalon.nueva(UUID.randomUUID(), UUID.randomUUID(), 0, INICIO, FIN, UUID.randomUUID())
        );
    }

    @Test
    void noDeberiaCrearReservaConNumInvitadosNegativo() {
        assertThrows(DomainException.class, () ->
                ReservaSalon.nueva(UUID.randomUUID(), UUID.randomUUID(), -10, INICIO, FIN, UUID.randomUUID())
        );
    }

    @Test
    void noDeberiaCrearReservaConFechaFinIgualAInicio() {
        assertThrows(DomainException.class, () ->
                ReservaSalon.nueva(UUID.randomUUID(), UUID.randomUUID(), 50, INICIO, INICIO, UUID.randomUUID())
        );
    }

    @Test
    void noDeberiaCrearReservaConFechaFinAnteriorAInicio() {
        assertThrows(DomainException.class, () ->
                ReservaSalon.nueva(UUID.randomUUID(), UUID.randomUUID(), 50, FIN, INICIO, UUID.randomUUID())
        );
    }

    @Test
    void noDeberiaCrearReservaConEventoIdNulo() {
        assertThrows(NullPointerException.class, () ->
                ReservaSalon.nueva(null, UUID.randomUUID(), 50, INICIO, FIN, UUID.randomUUID())
        );
    }

    @Test
    void noDeberiaCrearReservaConSalonIdNulo() {
        assertThrows(NullPointerException.class, () ->
                ReservaSalon.nueva(UUID.randomUUID(), null, 50, INICIO, FIN, UUID.randomUUID())
        );
    }

    @Test
    void noDeberiaCrearReservaConCreadoPorNulo() {
        assertThrows(NullPointerException.class, () ->
                ReservaSalon.nueva(UUID.randomUUID(), UUID.randomUUID(), 50, INICIO, FIN, null)
        );
    }

    @Test
    void noDeberiaReconstruirReservaConVersionCero() {
        assertThrows(DomainException.class, () ->
                ReservaSalon.reconstruir(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        UUID.randomUUID(), 50, INICIO, FIN, 0, true, UUID.randomUUID())
        );
    }

    @Test
    void noDeberiaReconstruirReservaConVersionNegativa() {
        assertThrows(DomainException.class, () ->
                ReservaSalon.reconstruir(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        UUID.randomUUID(), 50, INICIO, FIN, -1, true, UUID.randomUUID())
        );
    }
}