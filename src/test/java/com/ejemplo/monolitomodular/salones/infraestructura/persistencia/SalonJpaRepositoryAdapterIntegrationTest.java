package com.ejemplo.monolitomodular.salones.infraestructura.persistencia;

import com.ejemplo.monolitomodular.eventos.infraestructura.persistencia.ReservaSalonJpaEntity;
import com.ejemplo.monolitomodular.eventos.infraestructura.persistencia.ReservaSalonJpaRepositoryAdapter;
import com.ejemplo.monolitomodular.eventos.infraestructura.persistencia.SpringDataReservaSalonJpaRepository;
import com.ejemplo.monolitomodular.salones.dominio.modelo.Salon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * PI030 — Persistencia de salones y ocupación
 * Tipo: Integración Bottom-up (Caja gris)
 * Requisito: DP-GESTION_EVENTOS-03
 *
 * Valida: guardar salones, listar salones, buscarTodosPorIds()
 * y buscarSalonesOcupados() mediante la capa JPA.
 */
@ExtendWith(MockitoExtension.class)
class SalonJpaRepositoryAdapterIntegrationTest {

    @Mock
    SpringDataSalonJpaRepository salonRepository;

    @Mock
    SpringDataReservaSalonJpaRepository reservaRepository;

    @InjectMocks
    SalonJpaRepositoryAdapter salonAdapter;

    @InjectMocks
    ReservaSalonJpaRepositoryAdapter reservaAdapter;

    private static final UUID SALON_ID_A = UUID.randomUUID();
    private static final UUID SALON_ID_B = UUID.randomUUID();
    private static final UUID SALON_ID_C = UUID.randomUUID();

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 7, 10, 18, 0);
    private static final LocalDateTime FIN    = LocalDateTime.of(2026, 7, 10, 22, 0);

    private SalonJpaEntity salonActivoA;
    private SalonJpaEntity salonActivoB;
    private SalonJpaEntity salonInactivo;

    @BeforeEach
    void setUp() {
        salonActivoA  = new SalonJpaEntity(SALON_ID_A, "Salón República", 120, "Principal", true,  LocalDateTime.now(), LocalDateTime.now());
        salonActivoB  = new SalonJpaEntity(SALON_ID_B, "Salón Boyacá",    80,  "Secundario", true,  LocalDateTime.now(), LocalDateTime.now());
        salonInactivo = new SalonJpaEntity(SALON_ID_C, "Salón Antiguo",   40,  "Inactivo",   false, LocalDateTime.now(), LocalDateTime.now());
    }

    // ──────────────────────────────────────────────────────────────────────
    // guardar()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarSalonActivoYReconstruirDominioSinPerdidaDeDatos() {
        when(salonRepository.save(any())).thenReturn(salonActivoA);

        Salon salon = Salon.reconstruir(SALON_ID_A, "Salón República", 120, "Principal", true);

        Salon resultado = salonAdapter.guardar(salon);

        assertNotNull(resultado);
        assertEquals(SALON_ID_A, resultado.getId());
        assertEquals("Salón República", resultado.getNombre());
        assertEquals(120, resultado.getCapacidad());
        assertEquals("Principal", resultado.getDescripcion());
        assertTrue(resultado.isActivo());
        verify(salonRepository, times(1)).save(any());
    }

    @Test
    void deberiaGuardarSalonInacticoYConservarEstado() {
        when(salonRepository.save(any())).thenReturn(salonInactivo);

        Salon salon = Salon.reconstruir(SALON_ID_C, "Salón Antiguo", 40, "Inactivo", false);

        Salon resultado = salonAdapter.guardar(salon);

        assertFalse(resultado.isActivo());
        assertEquals(SALON_ID_C, resultado.getId());
    }

    // ──────────────────────────────────────────────────────────────────────
    // listar()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaListarSalonesOrdenadosPorNombre() {
        when(salonRepository.findAllByOrderByNombreAsc())
                .thenReturn(List.of(salonActivoB, salonActivoA));

        List<Salon> resultado = salonAdapter.listar();

        assertEquals(2, resultado.size());
        assertEquals("Salón Boyacá", resultado.get(0).getNombre());
        assertEquals("Salón República", resultado.get(1).getNombre());
    }

    @Test
    void deberiaListarSalonesActivosEInactivos() {
        when(salonRepository.findAllByOrderByNombreAsc())
                .thenReturn(List.of(salonActivoA, salonInactivo));

        List<Salon> resultado = salonAdapter.listar();

        assertEquals(2, resultado.size());
        long activos   = resultado.stream().filter(Salon::isActivo).count();
        long inactivos = resultado.stream().filter(s -> !s.isActivo()).count();
        assertEquals(1, activos);
        assertEquals(1, inactivos);
    }

    // ──────────────────────────────────────────────────────────────────────
    // buscarTodosPorIds()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarTodosPorIdsYRetornarSalonesCorrespondientes() {
        Collection<UUID> ids = Set.of(SALON_ID_A, SALON_ID_B);
        when(salonRepository.findByIdIn(ids)).thenReturn(List.of(salonActivoA, salonActivoB));

        List<Salon> resultado = salonAdapter.buscarTodosPorIds(ids);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(s -> s.getId().equals(SALON_ID_A)));
        assertTrue(resultado.stream().anyMatch(s -> s.getId().equals(SALON_ID_B)));
    }

    @Test
    void deberiaBuscarTodosPorIdsYRetornarVacioSiNoExistenSalones() {
        UUID idInexistente = UUID.randomUUID();
        when(salonRepository.findByIdIn(Set.of(idInexistente))).thenReturn(List.of());

        List<Salon> resultado = salonAdapter.buscarTodosPorIds(Set.of(idInexistente));

        assertTrue(resultado.isEmpty());
    }

    // ──────────────────────────────────────────────────────────────────────
    // existePorNombre()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaVerificarExistenciaPorNombreIgnorandoCapitalizacion() {
        when(salonRepository.existsByNombreIgnoreCase("Salón República")).thenReturn(true);
        when(salonRepository.existsByNombreIgnoreCase("salón república")).thenReturn(true);
        when(salonRepository.existsByNombreIgnoreCase("Salón Nuevo")).thenReturn(false);

        assertTrue(salonAdapter.existePorNombre("Salón República"));
        assertTrue(salonAdapter.existePorNombre("salón república"));
        assertFalse(salonAdapter.existePorNombre("Salón Nuevo"));
    }

    // ──────────────────────────────────────────────────────────────────────
    // buscarSalonesOcupados() — via ReservaSalonJpaRepositoryAdapter
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarSalonesOcupadosEnElRangoHorario() {
        when(reservaRepository.buscarSalonesOcupados(INICIO, FIN))
                .thenReturn(Set.of(SALON_ID_A));

        Set<UUID> ocupados = reservaAdapter.buscarSalonesOcupados(INICIO, FIN);

        assertEquals(1, ocupados.size());
        assertTrue(ocupados.contains(SALON_ID_A));
        assertFalse(ocupados.contains(SALON_ID_B));
    }

    @Test
    void deberiaRetornarSetVacioCuandoNoHaySalonesOcupados() {
        when(reservaRepository.buscarSalonesOcupados(INICIO, FIN)).thenReturn(Set.of());

        Set<UUID> ocupados = reservaAdapter.buscarSalonesOcupados(INICIO, FIN);

        assertTrue(ocupados.isEmpty());
    }

    @Test
    void deberiaRetornarMultiplesSalonesOcupadosCuandoHayVariasReservas() {
        when(reservaRepository.buscarSalonesOcupados(INICIO, FIN))
                .thenReturn(Set.of(SALON_ID_A, SALON_ID_B));

        Set<UUID> ocupados = reservaAdapter.buscarSalonesOcupados(INICIO, FIN);

        assertEquals(2, ocupados.size());
        assertTrue(ocupados.contains(SALON_ID_A));
        assertTrue(ocupados.contains(SALON_ID_B));
    }
}