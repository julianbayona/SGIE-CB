package com.ejemplo.monolitomodular.montajes.infraestructura.persistencia;

import com.ejemplo.monolitomodular.montajes.dominio.modelo.AdicionalEvento;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.InfraestructuraReserva;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.Montaje;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.MontajeMesaReserva;
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
import static org.mockito.Mockito.*;

/**
 * Pruebas de integración bottom-up para MontajeJpaRepositoryAdapter.
 * Cubre la traducción dominio ↔ entidad JPA y los métodos:
 * guardar() y buscarPorReservaId().
 * Sube el coverage del paquete persistencia de montajes (3% → ~75%).
 */
@ExtendWith(MockitoExtension.class)
class MontajeJpaRepositoryAdapterTest {

    @Mock
    SpringDataMontajeJpaRepository montajeRepository;

    @Mock
    SpringDataMontajeMesaReservaJpaRepository mesasRepository;

    @Mock
    SpringDataInfraestructuraReservaJpaRepository infraestructuraRepository;

    @Mock
    SpringDataAdicionalEventoJpaRepository adicionalRepository;

    @InjectMocks
    MontajeJpaRepositoryAdapter adapter;

    private static final UUID MONTAJE_ID       = UUID.randomUUID();
    private static final UUID RESERVA_ID       = UUID.randomUUID();
    private static final UUID TIPO_MESA_ID     = UUID.randomUUID();
    private static final UUID TIPO_SILLA_ID    = UUID.randomUUID();
    private static final UUID MANTEL_ID        = UUID.randomUUID();
    private static final UUID SOBREMANTEL_ID   = UUID.randomUUID();
    private static final UUID TIPO_ADICIONAL_ID = UUID.randomUUID();
    private static final UUID MESA_ID          = UUID.randomUUID();
    private static final UUID INFRA_ID         = UUID.randomUUID();
    private static final UUID ADICIONAL_ID     = UUID.randomUUID();

    private MontajeJpaEntity montajeEntity;
    private MontajeMesaReservaJpaEntity mesaEntity;
    private InfraestructuraReservaJpaEntity infraEntity;
    private AdicionalEventoJpaEntity adicionalEntity;

    @BeforeEach
    void setUp() {
        montajeEntity = new MontajeJpaEntity(
                MONTAJE_ID, RESERVA_ID, "Observaciones de prueba",
                LocalDateTime.now(), LocalDateTime.now()
        );

        mesaEntity = new MontajeMesaReservaJpaEntity(
                MESA_ID, MONTAJE_ID, TIPO_MESA_ID, TIPO_SILLA_ID,
                6, 10, MANTEL_ID, SOBREMANTEL_ID, true, false
        );

        infraEntity = new InfraestructuraReservaJpaEntity(
                INFRA_ID, MONTAJE_ID, true, false, true, false
        );

        adicionalEntity = new AdicionalEventoJpaEntity(
                ADICIONAL_ID, MONTAJE_ID, TIPO_ADICIONAL_ID, 3
        );
    }

    // ──────────────────────────────────────────────────────────────────────
    // buscarPorReservaId()
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarMontajePorReservaIdYRetornarPresente() {
        when(montajeRepository.findByReservaId(RESERVA_ID)).thenReturn(Optional.of(montajeEntity));
        when(mesasRepository.findByMontajeId(MONTAJE_ID)).thenReturn(List.of(mesaEntity));
        when(infraestructuraRepository.findByMontajeId(MONTAJE_ID)).thenReturn(Optional.of(infraEntity));
        when(adicionalRepository.findByMontajeId(MONTAJE_ID)).thenReturn(List.of(adicionalEntity));

        Optional<Montaje> resultado = adapter.buscarPorReservaId(RESERVA_ID);

        assertTrue(resultado.isPresent());
        Montaje montaje = resultado.get();
        assertEquals(MONTAJE_ID, montaje.getId());
        assertEquals(RESERVA_ID, montaje.getReservaId());
        assertEquals("Observaciones de prueba", montaje.getObservaciones());
        assertEquals(1, montaje.getMesas().size());
        assertEquals(1, montaje.getAdicionales().size());
        assertNotNull(montaje.getInfraestructura());
    }

    @Test
    void deberiaBuscarMontajePorReservaIdYRetornarVacioCuandoNoExiste() {
        when(montajeRepository.findByReservaId(RESERVA_ID)).thenReturn(Optional.empty());

        Optional<Montaje> resultado = adapter.buscarPorReservaId(RESERVA_ID);

        assertFalse(resultado.isPresent());
        verify(mesasRepository, never()).findByMontajeId(any());
    }

    @Test
    void deberiaReconstruirMesaConTodosLosCamposCorrectamente() {
        when(montajeRepository.findByReservaId(RESERVA_ID)).thenReturn(Optional.of(montajeEntity));
        when(mesasRepository.findByMontajeId(MONTAJE_ID)).thenReturn(List.of(mesaEntity));
        when(infraestructuraRepository.findByMontajeId(MONTAJE_ID)).thenReturn(Optional.of(infraEntity));
        when(adicionalRepository.findByMontajeId(MONTAJE_ID)).thenReturn(List.of());

        Optional<Montaje> resultado = adapter.buscarPorReservaId(RESERVA_ID);

        assertTrue(resultado.isPresent());
        MontajeMesaReserva mesa = resultado.get().getMesas().get(0);
        assertEquals(MESA_ID, mesa.getId());
        assertEquals(MONTAJE_ID, mesa.getMontajeId());
        assertEquals(TIPO_MESA_ID, mesa.getTipoMesaId());
        assertEquals(TIPO_SILLA_ID, mesa.getTipoSillaId());
        assertEquals(6, mesa.getSillaPorMesa());
        assertEquals(10, mesa.getCantidadMesas());
        assertEquals(MANTEL_ID, mesa.getMantelId());
        assertEquals(SOBREMANTEL_ID, mesa.getSobremantelId());
        assertTrue(mesa.isVajilla());
        assertFalse(mesa.isFajon());
    }

    @Test
    void deberiaReconstruirInfraestructuraConTodosLosCamposCorrectamente() {
        when(montajeRepository.findByReservaId(RESERVA_ID)).thenReturn(Optional.of(montajeEntity));
        when(mesasRepository.findByMontajeId(MONTAJE_ID)).thenReturn(List.of(mesaEntity));
        when(infraestructuraRepository.findByMontajeId(MONTAJE_ID)).thenReturn(Optional.of(infraEntity));
        when(adicionalRepository.findByMontajeId(MONTAJE_ID)).thenReturn(List.of());

        Optional<Montaje> resultado = adapter.buscarPorReservaId(RESERVA_ID);

        assertTrue(resultado.isPresent());
        InfraestructuraReserva infra = resultado.get().getInfraestructura();
        assertEquals(INFRA_ID, infra.getId());
        assertEquals(MONTAJE_ID, infra.getMontajeId());
        assertTrue(infra.isMesaPonque());
        assertFalse(infra.isMesaRegalos());
        assertTrue(infra.isEspacioMusicos());
        assertFalse(infra.isEstanteBombas());
    }

    @Test
    void deberiaReconstruirAdicionalConTodosLosCamposCorrectamente() {
        when(montajeRepository.findByReservaId(RESERVA_ID)).thenReturn(Optional.of(montajeEntity));
        when(mesasRepository.findByMontajeId(MONTAJE_ID)).thenReturn(List.of(mesaEntity));
        when(infraestructuraRepository.findByMontajeId(MONTAJE_ID)).thenReturn(Optional.of(infraEntity));
        when(adicionalRepository.findByMontajeId(MONTAJE_ID)).thenReturn(List.of(adicionalEntity));

        Optional<Montaje> resultado = adapter.buscarPorReservaId(RESERVA_ID);

        assertTrue(resultado.isPresent());
        AdicionalEvento adicional = resultado.get().getAdicionales().get(0);
        assertEquals(ADICIONAL_ID, adicional.getId());
        assertEquals(MONTAJE_ID, adicional.getMontajeId());
        assertEquals(TIPO_ADICIONAL_ID, adicional.getTipoAdicionalId());
        assertEquals(3, adicional.getCantidad());
    }

    @Test
    void deberiaBuscarMontajeConMultiplesMesasYAdicionales() {
        MontajeMesaReservaJpaEntity mesa2 = new MontajeMesaReservaJpaEntity(
                UUID.randomUUID(), MONTAJE_ID, UUID.randomUUID(), UUID.randomUUID(),
                4, 5, UUID.randomUUID(), null, false, true
        );
        AdicionalEventoJpaEntity adicional2 = new AdicionalEventoJpaEntity(
                UUID.randomUUID(), MONTAJE_ID, UUID.randomUUID(), 1
        );

        when(montajeRepository.findByReservaId(RESERVA_ID)).thenReturn(Optional.of(montajeEntity));
        when(mesasRepository.findByMontajeId(MONTAJE_ID)).thenReturn(List.of(mesaEntity, mesa2));
        when(infraestructuraRepository.findByMontajeId(MONTAJE_ID)).thenReturn(Optional.of(infraEntity));
        when(adicionalRepository.findByMontajeId(MONTAJE_ID)).thenReturn(List.of(adicionalEntity, adicional2));

        Optional<Montaje> resultado = adapter.buscarPorReservaId(RESERVA_ID);

        assertTrue(resultado.isPresent());
        assertEquals(2, resultado.get().getMesas().size());
        assertEquals(2, resultado.get().getAdicionales().size());
    }

    // ──────────────────────────────────────────────────────────────────────
    // guardar() — delegación a repositorios internos
    // ──────────────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarMontajeYDelegarATodosLosRepositorios() {
        when(montajeRepository.save(any())).thenReturn(montajeEntity);
        when(mesasRepository.saveAll(any())).thenReturn(List.of(mesaEntity));
        when(infraestructuraRepository.save(any())).thenReturn(infraEntity);
        when(adicionalRepository.saveAll(any())).thenReturn(List.of(adicionalEntity));
        // buscarPorReservaId interno después del save
        when(montajeRepository.findByReservaId(any())).thenReturn(Optional.of(montajeEntity));
        when(mesasRepository.findByMontajeId(any())).thenReturn(List.of(mesaEntity));
        when(infraestructuraRepository.findByMontajeId(any())).thenReturn(Optional.of(infraEntity));
        when(adicionalRepository.findByMontajeId(any())).thenReturn(List.of(adicionalEntity));

        MontajeMesaReserva mesa = MontajeMesaReserva.reconstruir(
                MESA_ID, MONTAJE_ID, TIPO_MESA_ID, TIPO_SILLA_ID,
                6, 10, MANTEL_ID, SOBREMANTEL_ID, true, false
        );
        InfraestructuraReserva infra = InfraestructuraReserva.reconstruir(
                INFRA_ID, MONTAJE_ID, true, false, true, false
        );
        AdicionalEvento adicional = AdicionalEvento.reconstruir(
                ADICIONAL_ID, MONTAJE_ID, TIPO_ADICIONAL_ID, 3
        );
        Montaje montaje = Montaje.reconstruir(
                MONTAJE_ID, RESERVA_ID, "Observaciones de prueba",
                List.of(mesa), infra, List.of(adicional)
        );

        Montaje resultado = adapter.guardar(montaje);

        assertNotNull(resultado);
        assertEquals(MONTAJE_ID, resultado.getId());
        assertEquals(RESERVA_ID, resultado.getReservaId());
        verify(montajeRepository, times(1)).save(any());
        verify(mesasRepository, times(1)).saveAll(any());
        verify(infraestructuraRepository, times(1)).save(any());
        verify(adicionalRepository, times(1)).saveAll(any());
    }
}