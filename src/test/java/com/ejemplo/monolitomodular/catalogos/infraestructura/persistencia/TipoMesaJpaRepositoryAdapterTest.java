package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoMesa;
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

@ExtendWith(MockitoExtension.class)
class TipoMesaJpaRepositoryAdapterTest {

    @Mock
    SpringDataTipoMesaJpaRepository repository;

    @InjectMocks
    TipoMesaJpaRepositoryAdapter adapter;

    private UUID tipoMesaId;
    private TipoMesaJpaEntity entityBase;

    @BeforeEach
    void setUp() {
        tipoMesaId = UUID.randomUUID();
        entityBase = new TipoMesaJpaEntity(
                tipoMesaId, "Mesa Redonda", true,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ── TipoMesaJpaEntity getters ────────────────────────────────────────────

    @Test
    void tipoMesaJpaEntityDeberiaExponerGetters() {
        assertEquals(tipoMesaId, entityBase.getId());
        assertEquals("Mesa Redonda", entityBase.getNombre());
        assertTrue(entityBase.isActivo());
    }

    @Test
    void tipoMesaJpaEntityInactivoDeberiaRetornarActivoFalse() {
        TipoMesaJpaEntity inactivo = new TipoMesaJpaEntity(
                tipoMesaId, "Mesa antigua", false,
                LocalDateTime.now(), LocalDateTime.now()
        );
        assertFalse(inactivo.isActivo());
        assertEquals("Mesa antigua", inactivo.getNombre());
    }

    // ── guardar ──────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarTipoMesaYRetornarDominio() {
        when(repository.save(any())).thenReturn(entityBase);

        TipoMesa tipoMesa = TipoMesa.reconstruir(tipoMesaId, "Mesa Redonda", true);
        TipoMesa resultado = adapter.guardar(tipoMesa);

        assertNotNull(resultado);
        assertEquals(tipoMesaId, resultado.getId());
        assertEquals("Mesa Redonda", resultado.getNombre());
        assertTrue(resultado.isActivo());
        verify(repository, times(1)).save(any());
    }

    @Test
    void deberiaGuardarTipoMesaInactivoCorrectamente() {
        TipoMesaJpaEntity entityInactiva = new TipoMesaJpaEntity(
                tipoMesaId, "Mesa antigua", false,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.save(any())).thenReturn(entityInactiva);

        TipoMesa tipoMesa = TipoMesa.reconstruir(tipoMesaId, "Mesa antigua", false);
        TipoMesa resultado = adapter.guardar(tipoMesa);

        assertFalse(resultado.isActivo());
    }

    // ── buscarPorId ──────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarPorIdYRetornarPresente() {
        when(repository.findById(tipoMesaId)).thenReturn(Optional.of(entityBase));

        Optional<TipoMesa> resultado = adapter.buscarPorId(tipoMesaId);

        assertTrue(resultado.isPresent());
        assertEquals(tipoMesaId, resultado.get().getId());
        assertEquals("Mesa Redonda", resultado.get().getNombre());
    }

    @Test
    void deberiaBuscarPorIdYRetornarVacioCuandoNoExiste() {
        when(repository.findById(tipoMesaId)).thenReturn(Optional.empty());

        Optional<TipoMesa> resultado = adapter.buscarPorId(tipoMesaId);

        assertFalse(resultado.isPresent());
    }

    // ── listar ───────────────────────────────────────────────────────────────

    @Test
    void deberiaListarTiposMesa() {
        TipoMesaJpaEntity otro = new TipoMesaJpaEntity(
                UUID.randomUUID(), "Mesa Cuadrada", true,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(entityBase, otro));

        List<TipoMesa> resultado = adapter.listar();

        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHayTiposMesa() {
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of());

        List<TipoMesa> resultado = adapter.listar();

        assertTrue(resultado.isEmpty());
    }

    // ── existeActivoPorId ────────────────────────────────────────────────────

    @Test
    void deberiaRetornarTrueSiExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(tipoMesaId)).thenReturn(true);

        assertTrue(adapter.existeActivoPorId(tipoMesaId));
    }

    @Test
    void deberiaRetornarFalseSiNoExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(tipoMesaId)).thenReturn(false);

        assertFalse(adapter.existeActivoPorId(tipoMesaId));
    }

    // ── existePorNombre ──────────────────────────────────────────────────────

    @Test
    void deberiaRetornarTrueSiExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Mesa Redonda")).thenReturn(true);

        assertTrue(adapter.existePorNombre("Mesa Redonda"));
    }

    @Test
    void deberiaRetornarFalseSiNoExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Mesa Inexistente")).thenReturn(false);

        assertFalse(adapter.existePorNombre("Mesa Inexistente"));
    }
}