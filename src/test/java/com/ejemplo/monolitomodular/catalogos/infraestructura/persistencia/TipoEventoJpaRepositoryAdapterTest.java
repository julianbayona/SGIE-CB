package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoEvento;
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
class TipoEventoJpaRepositoryAdapterTest {

    @Mock
    SpringDataTipoEventoJpaRepository repository;

    @InjectMocks
    TipoEventoJpaRepositoryAdapter adapter;

    private UUID tipoEventoId;
    private TipoEventoJpaEntity entityBase;

    @BeforeEach
    void setUp() {
        tipoEventoId = UUID.randomUUID();
        entityBase = new TipoEventoJpaEntity(
                tipoEventoId, "Matrimonio", "Evento de matrimonio formal", true,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ── TipoEventoJpaEntity getters ──────────────────────────────────────────

    @Test
    void tipoEventoJpaEntityDeberiaExponerGetters() {
        assertEquals(tipoEventoId, entityBase.getId());
        assertEquals("Matrimonio", entityBase.getNombre());
        assertEquals("Evento de matrimonio formal", entityBase.getDescripcion());
        assertTrue(entityBase.isActivo());
    }

    @Test
    void tipoEventoJpaEntityConDescripcionNulaDeberiaPermitirlo() {
        TipoEventoJpaEntity sinDescripcion = new TipoEventoJpaEntity(
                UUID.randomUUID(), "Cumpleaños", null, true,
                LocalDateTime.now(), LocalDateTime.now()
        );
        assertNull(sinDescripcion.getDescripcion());
        assertEquals("Cumpleaños", sinDescripcion.getNombre());
    }

    @Test
    void tipoEventoJpaEntityInactivoDeberiaRetornarActivoFalse() {
        TipoEventoJpaEntity inactivo = new TipoEventoJpaEntity(
                tipoEventoId, "Evento obsoleto", "desc", false,
                LocalDateTime.now(), LocalDateTime.now()
        );
        assertFalse(inactivo.isActivo());
    }

    // ── guardar ──────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarTipoEventoYRetornarDominio() {
        when(repository.save(any())).thenReturn(entityBase);

        TipoEvento tipoEvento = TipoEvento.reconstruir(
                tipoEventoId, "Matrimonio", "Evento de matrimonio formal", true
        );
        TipoEvento resultado = adapter.guardar(tipoEvento);

        assertNotNull(resultado);
        assertEquals(tipoEventoId, resultado.getId());
        assertEquals("Matrimonio", resultado.getNombre());
        assertEquals("Evento de matrimonio formal", resultado.getDescripcion());
        assertTrue(resultado.isActivo());
        verify(repository, times(1)).save(any());
    }

    @Test
    void deberiaGuardarTipoEventoInactivoCorrectamente() {
        TipoEventoJpaEntity entityInactiva = new TipoEventoJpaEntity(
                tipoEventoId, "Evento obsoleto", "desc", false,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.save(any())).thenReturn(entityInactiva);

        TipoEvento tipoEvento = TipoEvento.reconstruir(tipoEventoId, "Evento obsoleto", "desc", false);
        TipoEvento resultado = adapter.guardar(tipoEvento);

        assertFalse(resultado.isActivo());
    }

    // ── buscarPorId ──────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarPorIdYRetornarPresente() {
        when(repository.findById(tipoEventoId)).thenReturn(Optional.of(entityBase));

        Optional<TipoEvento> resultado = adapter.buscarPorId(tipoEventoId);

        assertTrue(resultado.isPresent());
        assertEquals(tipoEventoId, resultado.get().getId());
        assertEquals("Matrimonio", resultado.get().getNombre());
    }

    @Test
    void deberiaBuscarPorIdYRetornarVacioCuandoNoExiste() {
        when(repository.findById(tipoEventoId)).thenReturn(Optional.empty());

        Optional<TipoEvento> resultado = adapter.buscarPorId(tipoEventoId);

        assertFalse(resultado.isPresent());
    }

    // ── listar ───────────────────────────────────────────────────────────────

    @Test
    void deberiaListarTiposEvento() {
        TipoEventoJpaEntity otro = new TipoEventoJpaEntity(
                UUID.randomUUID(), "Quinceaños", "Celebración de quinceaños", true,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(entityBase, otro));

        List<TipoEvento> resultado = adapter.listar();

        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHayTiposEvento() {
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of());

        List<TipoEvento> resultado = adapter.listar();

        assertTrue(resultado.isEmpty());
    }

    // ── existeActivoPorId ────────────────────────────────────────────────────

    @Test
    void deberiaRetornarTrueSiExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(tipoEventoId)).thenReturn(true);

        assertTrue(adapter.existeActivoPorId(tipoEventoId));
    }

    @Test
    void deberiaRetornarFalseSiNoExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(tipoEventoId)).thenReturn(false);

        assertFalse(adapter.existeActivoPorId(tipoEventoId));
    }

    // ── existePorNombre ──────────────────────────────────────────────────────

    @Test
    void deberiaRetornarTrueSiExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Matrimonio")).thenReturn(true);

        assertTrue(adapter.existePorNombre("Matrimonio"));
    }

    @Test
    void deberiaRetornarFalseSiNoExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Evento Inexistente")).thenReturn(false);

        assertFalse(adapter.existePorNombre("Evento Inexistente"));
    }
}