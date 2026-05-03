package com.ejemplo.monolitomodular.eventos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.HistorialEstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * PI031 — Persistencia de evento, historial y reservas vigentes
 * Tipo: Integración Bottom-up (Caja gris)
 * Requisito: DP-GESTION_EVENTOS-02
 *
 * Valida: guardar(), buscarPorId(), listar(), guardar historial,
 * listarPorEvento() y desactivarReservaVigente() mediante la capa JPA.
 */
@ExtendWith(MockitoExtension.class)
class EventoJpaRepositoryAdapterIntegrationTest {

    // ── Evento ────────────────────────────────────────────────────────────
    @Mock
    SpringDataEventoJpaRepository eventoRepository;

    @InjectMocks
    EventoJpaRepositoryAdapter eventoAdapter;

    // ── Historial ─────────────────────────────────────────────────────────
    @Mock
    SpringDataHistorialEstadoEventoJpaRepository historialRepository;

    @InjectMocks
    HistorialEstadoEventoJpaRepositoryAdapter historialAdapter;

    // ── Reserva ───────────────────────────────────────────────────────────
    @Mock
    SpringDataReservaSalonJpaRepository reservaRepository;

    @InjectMocks
    ReservaSalonJpaRepositoryAdapter reservaAdapter;

    // ── Constantes ────────────────────────────────────────────────────────
    private static final UUID EVENTO_ID    = UUID.randomUUID();
    private static final UUID CLIENTE_ID   = UUID.randomUUID();
    private static final UUID TIPO_EVENTO  = UUID.randomUUID();
    private static final UUID TIPO_COMIDA  = UUID.randomUUID();
    private static final UUID USUARIO_ID   = UUID.randomUUID();
    private static final UUID SALON_ID     = UUID.randomUUID();
    private static final UUID RESERVA_ID   = UUID.randomUUID();
    private static final UUID RAIZ_ID      = UUID.randomUUID();

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 8, 20, 18, 0);
    private static final LocalDateTime FIN    = LocalDateTime.of(2026, 8, 20, 22, 0);

    private EventoJpaEntity eventoEntity;
    private ReservaSalonJpaEntity reservaEntity;
    private HistorialEstadoEventoJpaEntity historialEntity;

    @BeforeEach
    void setUp() {
        eventoEntity = new EventoJpaEntity(
                EVENTO_ID, CLIENTE_ID, TIPO_EVENTO, TIPO_COMIDA, USUARIO_ID,
                INICIO, FIN, EstadoEvento.PENDIENTE, null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        reservaEntity = new ReservaSalonJpaEntity(
                RESERVA_ID, RAIZ_ID, EVENTO_ID, SALON_ID,
                100, INICIO, FIN, 1, true, USUARIO_ID,
                LocalDateTime.now(), LocalDateTime.now()
        );

        historialEntity = new HistorialEstadoEventoJpaEntity(
                UUID.randomUUID(), EVENTO_ID, USUARIO_ID,
                null, EstadoEvento.PENDIENTE, LocalDateTime.now()
        );
    }

    // ──────────────────────────────────────────────────────────────────────
    // Evento — guardar()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarEventoYReconstruirDominioSinPerdidaDeDatos() {
        when(eventoRepository.save(any())).thenReturn(eventoEntity);

        Evento evento = Evento.reconstruir(
                EVENTO_ID, CLIENTE_ID, TIPO_EVENTO, TIPO_COMIDA,
                USUARIO_ID, INICIO, FIN, EstadoEvento.PENDIENTE, null
        );

        Evento resultado = eventoAdapter.guardar(evento);

        assertNotNull(resultado);
        assertEquals(EVENTO_ID, resultado.getId());
        assertEquals(CLIENTE_ID, resultado.getClienteId());
        assertEquals(TIPO_EVENTO, resultado.getTipoEventoId());
        assertEquals(TIPO_COMIDA, resultado.getTipoComidaId());
        assertEquals(USUARIO_ID, resultado.getUsuarioCreadorId());
        assertEquals(EstadoEvento.PENDIENTE, resultado.getEstado());
        assertEquals(INICIO, resultado.getFechaHoraInicio());
        assertEquals(FIN, resultado.getFechaHoraFin());
        assertNull(resultado.getGcalEventId());
        verify(eventoRepository, times(1)).save(any());
    }

    // ──────────────────────────────────────────────────────────────────────
    // Evento — buscarPorId()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarEventoPorIdYRetornarPresente() {
        when(eventoRepository.findById(EVENTO_ID)).thenReturn(Optional.of(eventoEntity));

        Optional<Evento> resultado = eventoAdapter.buscarPorId(EVENTO_ID);

        assertTrue(resultado.isPresent());
        assertEquals(EVENTO_ID, resultado.get().getId());
        assertEquals(EstadoEvento.PENDIENTE, resultado.get().getEstado());
    }

    @Test
    void deberiaBuscarEventoPorIdYRetornarVacioCuandoNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(eventoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        Optional<Evento> resultado = eventoAdapter.buscarPorId(idInexistente);

        assertFalse(resultado.isPresent());
    }

    // ──────────────────────────────────────────────────────────────────────
    // Evento — listar()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaListarEventosOrdenadosPorFechaCreacion() {
        EventoJpaEntity otroEvento = new EventoJpaEntity(
                UUID.randomUUID(), CLIENTE_ID, TIPO_EVENTO, TIPO_COMIDA, USUARIO_ID,
                INICIO.plusDays(1), FIN.plusDays(1), EstadoEvento.PENDIENTE, null,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(eventoRepository.findAllByOrderByCreatedAtAsc())
                .thenReturn(List.of(eventoEntity, otroEvento));

        List<Evento> resultado = eventoAdapter.listar();

        assertEquals(2, resultado.size());
        assertEquals(EVENTO_ID, resultado.get(0).getId());
    }

    // ──────────────────────────────────────────────────────────────────────
    // Historial — guardar()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarHistorialDeCreacionYRetornarDominioReconstruido() {
        when(historialRepository.save(any())).thenReturn(historialEntity);

        HistorialEstadoEvento historial = HistorialEstadoEvento.registrarCreacion(
                EVENTO_ID, USUARIO_ID
        );

        HistorialEstadoEvento resultado = historialAdapter.guardar(historial);

        assertNotNull(resultado);
        assertEquals(EVENTO_ID, resultado.getEventoId());
        assertEquals(USUARIO_ID, resultado.getUsuarioId());
        assertNull(resultado.getEstadoAnterior());
        assertEquals(EstadoEvento.PENDIENTE, resultado.getEstadoNuevo());
        verify(historialRepository, times(1)).save(any());
    }

    // ──────────────────────────────────────────────────────────────────────
    // Reserva — guardar() y listarPorEvento()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarReservaVigenteYReconstruirDominioSinPerdidaDeDatos() {
        when(reservaRepository.save(any())).thenReturn(reservaEntity);

        ReservaSalon reserva = ReservaSalon.reconstruir(
                RESERVA_ID, RAIZ_ID, EVENTO_ID, SALON_ID,
                100, INICIO, FIN, 1, true, USUARIO_ID
        );

        ReservaSalon resultado = reservaAdapter.guardar(reserva);

        assertNotNull(resultado);
        assertEquals(RESERVA_ID, resultado.getId());
        assertEquals(RAIZ_ID, resultado.getReservaRaizId());
        assertEquals(EVENTO_ID, resultado.getEventoId());
        assertEquals(SALON_ID, resultado.getSalonId());
        assertEquals(100, resultado.getNumInvitados());
        assertEquals(1, resultado.getVersion());
        assertTrue(resultado.isVigente());
        verify(reservaRepository, times(1)).save(any());
    }

    @Test
    void deberiaListarReservasVigentesPorEventoYRetornarSoloLasVigentes() {
        when(reservaRepository.findByEventoIdAndVigenteTrue(EVENTO_ID))
                .thenReturn(List.of(reservaEntity));

        List<ReservaSalon> resultado = reservaAdapter.listarPorEvento(EVENTO_ID);

        assertEquals(1, resultado.size());
        assertEquals(RESERVA_ID, resultado.get(0).getId());
        assertTrue(resultado.get(0).isVigente());
    }

    @Test
    void deberiaListarReservasPorEventoYRetornarListaVaciaSiNoHayVigentes() {
        UUID eventoSinReservas = UUID.randomUUID();
        when(reservaRepository.findByEventoIdAndVigenteTrue(eventoSinReservas))
                .thenReturn(List.of());

        List<ReservaSalon> resultado = reservaAdapter.listarPorEvento(eventoSinReservas);

        assertTrue(resultado.isEmpty());
    }

    // ──────────────────────────────────────────────────────────────────────
    // Reserva — desactivarReservaVigente() y versionamiento
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaDesactivarReservaVigenteAntesDePersistirNuevaVersion() {
        // Simula: desactivar versión vigente (método devuelve int: número de filas actualizadas)
        when(reservaRepository.desactivarReservaVigente(eq(RAIZ_ID), any(LocalDateTime.class)))
                .thenReturn(1);

        reservaAdapter.desactivarReservaVigente(RAIZ_ID);

        verify(reservaRepository, times(1))
                .desactivarReservaVigente(eq(RAIZ_ID), any(LocalDateTime.class));
    }

    @Test
    void deberiaGuardarNuevaVersionDeReservaConVersionIncrementada() {
        ReservaSalonJpaEntity entityV2 = new ReservaSalonJpaEntity(
                UUID.randomUUID(), RAIZ_ID, EVENTO_ID, SALON_ID,
                120, INICIO.plusHours(1), FIN.plusHours(1), 2, true, USUARIO_ID,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(reservaRepository.save(any())).thenReturn(entityV2);

        ReservaSalon reservaV2 = ReservaSalon.reconstruir(
                entityV2.getId(), RAIZ_ID, EVENTO_ID, SALON_ID,
                120, INICIO.plusHours(1), FIN.plusHours(1), 2, true, USUARIO_ID
        );

        ReservaSalon resultado = reservaAdapter.guardar(reservaV2);

        assertEquals(2, resultado.getVersion());
        assertTrue(resultado.isVigente());
        assertEquals(RAIZ_ID, resultado.getReservaRaizId());
    }

    // ──────────────────────────────────────────────────────────────────────
    // Reserva — buscarVigentePorRaizId()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarReservaVigentePorRaizIdYRetornarPresente() {
        when(reservaRepository.findByReservaRaizIdAndVigenteTrue(RAIZ_ID))
                .thenReturn(Optional.of(reservaEntity));

        Optional<ReservaSalon> resultado = reservaAdapter.buscarVigentePorRaizId(RAIZ_ID);

        assertTrue(resultado.isPresent());
        assertEquals(RAIZ_ID, resultado.get().getReservaRaizId());
        assertTrue(resultado.get().isVigente());
    }

    @Test
    void deberiaBuscarReservaVigentePorRaizIdYRetornarVacioCuandoNoExiste() {
        UUID raizInexistente = UUID.randomUUID();
        when(reservaRepository.findByReservaRaizIdAndVigenteTrue(raizInexistente))
                .thenReturn(Optional.empty());

        Optional<ReservaSalon> resultado = reservaAdapter.buscarVigentePorRaizId(raizInexistente);

        assertFalse(resultado.isPresent());
    }
}