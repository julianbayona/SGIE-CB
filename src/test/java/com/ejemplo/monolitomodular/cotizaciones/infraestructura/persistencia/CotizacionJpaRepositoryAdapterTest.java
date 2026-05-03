package com.ejemplo.monolitomodular.cotizaciones.infraestructura.persistencia;

import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.CotizacionItem;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas de integración bottom-up para CotizacionJpaRepositoryAdapter.
 * Cubre la traducción dominio ↔ entidad JPA y los métodos:
 * guardar(), buscarPorId(), buscarActivaPorReservaId(),
 * buscarUltimaPorReservaRaizId() y desactualizarActivasPorReservaId().
 * Sube el coverage del paquete persistencia cotizaciones (5% → ~70%).
 */
@ExtendWith(MockitoExtension.class)
class CotizacionJpaRepositoryAdapterTest {

    @Mock
    SpringDataCotizacionJpaRepository cotizacionRepository;

    @Mock
    SpringDataCotizacionItemJpaRepository itemRepository;

    @InjectMocks
    CotizacionJpaRepositoryAdapter adapter;

    private static final UUID COTIZACION_ID = UUID.randomUUID();
    private static final UUID RESERVA_ID    = UUID.randomUUID();
    private static final UUID USUARIO_ID    = UUID.randomUUID();
    private static final UUID ITEM_ID       = UUID.randomUUID();
    private static final UUID ORIGEN_ID     = UUID.randomUUID();

    private CotizacionJpaEntity cotizacionEntity;
    private CotizacionItemJpaEntity itemEntity;

    @BeforeEach
    void setUp() {
        cotizacionEntity = new CotizacionJpaEntity(
                COTIZACION_ID, RESERVA_ID, USUARIO_ID,
                EstadoCotizacion.BORRADOR,
                new BigDecimal("100000"),
                new BigDecimal("5000"),
                new BigDecimal("95000"),
                "Observaciones de prueba",
                LocalDateTime.now(), LocalDateTime.now()
        );

        itemEntity = new CotizacionItemJpaEntity(
                ITEM_ID, COTIZACION_ID, "MENU", ORIGEN_ID,
                "Plato especial", new BigDecimal("50000"), null,
                2, new BigDecimal("100000")
        );
    }

    // ──────────────────────────────────────────────────────────────────────
    // buscarPorId()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarCotizacionPorIdYRetornarPresente() {
        when(cotizacionRepository.findById(COTIZACION_ID)).thenReturn(Optional.of(cotizacionEntity));
        when(itemRepository.findByCotizacionId(COTIZACION_ID)).thenReturn(List.of(itemEntity));

        Optional<Cotizacion> resultado = adapter.buscarPorId(COTIZACION_ID);

        assertTrue(resultado.isPresent());
        Cotizacion cotizacion = resultado.get();
        assertEquals(COTIZACION_ID, cotizacion.getId());
        assertEquals(RESERVA_ID, cotizacion.getReservaId());
        assertEquals(USUARIO_ID, cotizacion.getUsuarioId());
        assertEquals(EstadoCotizacion.BORRADOR, cotizacion.getEstado());
        assertEquals(1, cotizacion.getItems().size());
    }

    @Test
    void deberiaBuscarCotizacionPorIdYRetornarVacioCuandoNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(cotizacionRepository.findById(idInexistente)).thenReturn(Optional.empty());

        Optional<Cotizacion> resultado = adapter.buscarPorId(idInexistente);

        assertFalse(resultado.isPresent());
        verify(itemRepository, never()).findByCotizacionId(any());
    }

    @Test
    void deberiaReconstruirItemConTodosLosCamposCorrectamente() {
        when(cotizacionRepository.findById(COTIZACION_ID)).thenReturn(Optional.of(cotizacionEntity));
        when(itemRepository.findByCotizacionId(COTIZACION_ID)).thenReturn(List.of(itemEntity));

        Optional<Cotizacion> resultado = adapter.buscarPorId(COTIZACION_ID);

        assertTrue(resultado.isPresent());
        CotizacionItem item = resultado.get().getItems().get(0);
        assertEquals(ITEM_ID, item.getId());
        assertEquals(COTIZACION_ID, item.getCotizacionId());
        assertEquals("MENU", item.getTipoConcepto());
        assertEquals(ORIGEN_ID, item.getOrigenId());
        assertEquals("Plato especial", item.getDescripcion());
        assertEquals(0, new BigDecimal("50000").compareTo(item.getPrecioBase()));
        assertNull(item.getPrecioOverride());
        assertEquals(2, item.getCantidad());
    }

    // ──────────────────────────────────────────────────────────────────────
    // buscarActivaPorReservaId()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarCotizacionActivaPorReservaIdYRetornarPresente() {
        when(cotizacionRepository.findByReservaIdAndEstadoNotInOrderByCreatedAtDesc(
                eq(RESERVA_ID), any())).thenReturn(List.of(cotizacionEntity));
        when(itemRepository.findByCotizacionId(COTIZACION_ID)).thenReturn(List.of(itemEntity));

        Optional<Cotizacion> resultado = adapter.buscarActivaPorReservaId(RESERVA_ID);

        assertTrue(resultado.isPresent());
        assertEquals(COTIZACION_ID, resultado.get().getId());
        assertEquals(EstadoCotizacion.BORRADOR, resultado.get().getEstado());
    }

    @Test
    void deberiaBuscarCotizacionActivaPorReservaIdYRetornarVaciaSiNoHayActiva() {
        when(cotizacionRepository.findByReservaIdAndEstadoNotInOrderByCreatedAtDesc(
                eq(RESERVA_ID), any())).thenReturn(List.of());

        Optional<Cotizacion> resultado = adapter.buscarActivaPorReservaId(RESERVA_ID);

        assertFalse(resultado.isPresent());
    }

    // ──────────────────────────────────────────────────────────────────────
    // buscarUltimaPorReservaRaizId()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarUltimaCotizacionPorReservaRaizIdYRetornarPresente() {
        UUID reservaRaizId = UUID.randomUUID();
        when(cotizacionRepository.findByReservaRaizIdOrderByCreatedAtDesc(reservaRaizId))
                .thenReturn(List.of(cotizacionEntity));
        when(itemRepository.findByCotizacionId(COTIZACION_ID)).thenReturn(List.of(itemEntity));

        Optional<Cotizacion> resultado = adapter.buscarUltimaPorReservaRaizId(reservaRaizId);

        assertTrue(resultado.isPresent());
        assertEquals(COTIZACION_ID, resultado.get().getId());
    }

    @Test
    void deberiaBuscarUltimaCotizacionPorReservaRaizIdYRetornarVaciaSiNoExiste() {
        UUID reservaRaizId = UUID.randomUUID();
        when(cotizacionRepository.findByReservaRaizIdOrderByCreatedAtDesc(reservaRaizId))
                .thenReturn(List.of());

        Optional<Cotizacion> resultado = adapter.buscarUltimaPorReservaRaizId(reservaRaizId);

        assertFalse(resultado.isPresent());
    }

    // ──────────────────────────────────────────────────────────────────────
    // desactualizarActivasPorReservaId()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaDesactualizarCotizacionesActivasPorReservaId() {
        when(cotizacionRepository.desactualizarActivasPorReservaId(eq(RESERVA_ID), any(LocalDateTime.class)))
                .thenReturn(1);

        adapter.desactualizarActivasPorReservaId(RESERVA_ID);

        verify(cotizacionRepository, times(1))
                .desactualizarActivasPorReservaId(eq(RESERVA_ID), any(LocalDateTime.class));
    }

    // ──────────────────────────────────────────────────────────────────────
    // guardar()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarCotizacionYReconstruirDominioSinPerdidaDeDatos() {
        when(cotizacionRepository.save(any())).thenReturn(cotizacionEntity);
        when(itemRepository.saveAll(any())).thenReturn(List.of(itemEntity));
        when(cotizacionRepository.findById(COTIZACION_ID)).thenReturn(Optional.of(cotizacionEntity));
        when(itemRepository.findByCotizacionId(COTIZACION_ID)).thenReturn(List.of(itemEntity));

        CotizacionItem item = CotizacionItem.reconstruir(
                ITEM_ID, COTIZACION_ID, "MENU", ORIGEN_ID,
                "Plato especial", new BigDecimal("50000"), null, 2
        );
        Cotizacion cotizacion = Cotizacion.reconstruir(
                COTIZACION_ID, RESERVA_ID, USUARIO_ID,
                EstadoCotizacion.BORRADOR, new BigDecimal("5000"),
                "Observaciones de prueba", List.of(item)
        );

        Cotizacion resultado = adapter.guardar(cotizacion);

        assertNotNull(resultado);
        assertEquals(COTIZACION_ID, resultado.getId());
        assertEquals(RESERVA_ID, resultado.getReservaId());
        assertEquals(EstadoCotizacion.BORRADOR, resultado.getEstado());
        assertFalse(resultado.getItems().isEmpty());
        verify(cotizacionRepository, times(1)).save(any());
        verify(itemRepository, times(1)).saveAll(any());
    }

    @Test
    void deberiaGuardarCotizacionConItemConPrecioOverride() {
        CotizacionItemJpaEntity itemConOverride = new CotizacionItemJpaEntity(
                ITEM_ID, COTIZACION_ID, "ADICIONAL", ORIGEN_ID,
                "Decoración", new BigDecimal("30000"), new BigDecimal("25000"),
                1, new BigDecimal("25000")
        );
        when(cotizacionRepository.save(any())).thenReturn(cotizacionEntity);
        when(itemRepository.saveAll(any())).thenReturn(List.of(itemConOverride));
        when(cotizacionRepository.findById(COTIZACION_ID)).thenReturn(Optional.of(cotizacionEntity));
        when(itemRepository.findByCotizacionId(COTIZACION_ID)).thenReturn(List.of(itemConOverride));

        CotizacionItem item = CotizacionItem.reconstruir(
                ITEM_ID, COTIZACION_ID, "ADICIONAL", ORIGEN_ID,
                "Decoración", new BigDecimal("30000"), new BigDecimal("25000"), 1
        );
        Cotizacion cotizacion = Cotizacion.reconstruir(
                COTIZACION_ID, RESERVA_ID, USUARIO_ID,
                EstadoCotizacion.BORRADOR, BigDecimal.ZERO, null, List.of(item)
        );

        Cotizacion resultado = adapter.guardar(cotizacion);

        assertNotNull(resultado);
        CotizacionItem itemResultado = resultado.getItems().get(0);
        assertEquals(new BigDecimal("25000"), itemResultado.getPrecioOverride());
        assertEquals("ADICIONAL", itemResultado.getTipoConcepto());
    }
}