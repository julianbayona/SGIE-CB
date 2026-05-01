package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.ModoCobroAdicional;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoAdicional;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoAdicionalJpaRepositoryAdapterTest {

    @Mock
    SpringDataTipoAdicionalJpaRepository repository;

    @InjectMocks
    TipoAdicionalJpaRepositoryAdapter adapter;

    private UUID tipoAdicionalId;
    private TipoAdicionalJpaEntity entityBase;

    @BeforeEach
    void setUp() {
        tipoAdicionalId = UUID.randomUUID();
        entityBase = new TipoAdicionalJpaEntity(
                tipoAdicionalId, "Decoración floral", ModoCobroAdicional.UNIDAD,
                new BigDecimal("50000.00"), true,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ── TipoAdicionalJpaEntity getters ───────────────────────────────────────

    @Test
    void tipoAdicionalJpaEntityDeberiaExponerGetters() {
        assertEquals(tipoAdicionalId, entityBase.getId());
        assertEquals("Decoración floral", entityBase.getNombre());
        assertEquals(ModoCobroAdicional.UNIDAD, entityBase.getModoCobro());
        assertEquals(new BigDecimal("50000.00"), entityBase.getPrecioBase());
        assertTrue(entityBase.isActivo());
    }

    @Test
    void tipoAdicionalJpaEntityConModoCobroServicioDeberiaExponerlo() {
        TipoAdicionalJpaEntity entity = new TipoAdicionalJpaEntity(
                UUID.randomUUID(), "Sonido", ModoCobroAdicional.SERVICIO,
                new BigDecimal("200000.00"), true,
                LocalDateTime.now(), LocalDateTime.now()
        );
        assertEquals(ModoCobroAdicional.SERVICIO, entity.getModoCobro());
        assertEquals(new BigDecimal("200000.00"), entity.getPrecioBase());
    }

    @Test
    void tipoAdicionalJpaEntityInactivoDeberiaRetornarActivoFalse() {
        TipoAdicionalJpaEntity inactivo = new TipoAdicionalJpaEntity(
                tipoAdicionalId, "Adicional viejo", ModoCobroAdicional.UNIDAD,
                BigDecimal.ZERO, false,
                LocalDateTime.now(), LocalDateTime.now()
        );
        assertFalse(inactivo.isActivo());
    }

    // ── guardar ──────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarTipoAdicionalYRetornarDominio() {
        when(repository.save(any())).thenReturn(entityBase);

        TipoAdicional tipoAdicional = TipoAdicional.reconstruir(
                tipoAdicionalId, "Decoración floral",
                ModoCobroAdicional.UNIDAD, new BigDecimal("50000.00"), true
        );
        TipoAdicional resultado = adapter.guardar(tipoAdicional);

        assertNotNull(resultado);
        assertEquals(tipoAdicionalId, resultado.getId());
        assertEquals("Decoración floral", resultado.getNombre());
        assertEquals(ModoCobroAdicional.UNIDAD, resultado.getModoCobro());
        assertEquals(new BigDecimal("50000.00"), resultado.getPrecioBase());
        assertTrue(resultado.isActivo());
        verify(repository, times(1)).save(any());
    }

    @Test
    void deberiaGuardarTipoAdicionalInactivoCorrectamente() {
        TipoAdicionalJpaEntity entityInactiva = new TipoAdicionalJpaEntity(
                tipoAdicionalId, "Adicional viejo", ModoCobroAdicional.SERVICIO,
                BigDecimal.ZERO, false,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.save(any())).thenReturn(entityInactiva);

        TipoAdicional tipoAdicional = TipoAdicional.reconstruir(
                tipoAdicionalId, "Adicional viejo",
                ModoCobroAdicional.SERVICIO, BigDecimal.ZERO, false
        );
        TipoAdicional resultado = adapter.guardar(tipoAdicional);

        assertFalse(resultado.isActivo());
    }

    // ── buscarPorId ──────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarPorIdYRetornarPresente() {
        when(repository.findById(tipoAdicionalId)).thenReturn(Optional.of(entityBase));

        Optional<TipoAdicional> resultado = adapter.buscarPorId(tipoAdicionalId);

        assertTrue(resultado.isPresent());
        assertEquals(tipoAdicionalId, resultado.get().getId());
        assertEquals(ModoCobroAdicional.UNIDAD, resultado.get().getModoCobro());
    }

    @Test
    void deberiaBuscarPorIdYRetornarVacioCuandoNoExiste() {
        when(repository.findById(tipoAdicionalId)).thenReturn(Optional.empty());

        Optional<TipoAdicional> resultado = adapter.buscarPorId(tipoAdicionalId);

        assertFalse(resultado.isPresent());
    }

    // ── listar ───────────────────────────────────────────────────────────────

    @Test
    void deberiaListarTiposAdicionales() {
        TipoAdicionalJpaEntity otro = new TipoAdicionalJpaEntity(
                UUID.randomUUID(), "Fotografía", ModoCobroAdicional.SERVICIO,
                new BigDecimal("150000.00"), true,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(entityBase, otro));

        List<TipoAdicional> resultado = adapter.listar();

        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHayTiposAdicionales() {
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of());

        List<TipoAdicional> resultado = adapter.listar();

        assertTrue(resultado.isEmpty());
    }

    // ── existeActivoPorId ────────────────────────────────────────────────────

    @Test
    void deberiaRetornarTrueSiExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(tipoAdicionalId)).thenReturn(true);

        assertTrue(adapter.existeActivoPorId(tipoAdicionalId));
    }

    @Test
    void deberiaRetornarFalseSiNoExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(tipoAdicionalId)).thenReturn(false);

        assertFalse(adapter.existeActivoPorId(tipoAdicionalId));
    }

    // ── existePorNombre ──────────────────────────────────────────────────────

    @Test
    void deberiaRetornarTrueSiExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Decoración floral")).thenReturn(true);

        assertTrue(adapter.existePorNombre("Decoración floral"));
    }

    @Test
    void deberiaRetornarFalseSiNoExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Adicional Inexistente")).thenReturn(false);

        assertFalse(adapter.existePorNombre("Adicional Inexistente"));
    }
}