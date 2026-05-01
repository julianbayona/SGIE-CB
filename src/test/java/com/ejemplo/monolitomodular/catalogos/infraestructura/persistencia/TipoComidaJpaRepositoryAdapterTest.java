package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoComida;
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
class TipoComidaJpaRepositoryAdapterTest {

    @Mock
    SpringDataTipoComidaJpaRepository repository;

    @InjectMocks
    TipoComidaJpaRepositoryAdapter adapter;

    private UUID tipoComidaId;
    private TipoComidaJpaEntity entityBase;

    @BeforeEach
    void setUp() {
        tipoComidaId = UUID.randomUUID();
        entityBase = new TipoComidaJpaEntity(
                tipoComidaId, "Buffet", "Servicio de buffet completo", true,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ── TipoComidaJpaEntity getters ──────────────────────────────────────────

    @Test
    void tipoComidaJpaEntityDeberiaExponerGetters() {
        assertEquals(tipoComidaId, entityBase.getId());
        assertEquals("Buffet", entityBase.getNombre());
        assertEquals("Servicio de buffet completo", entityBase.getDescripcion());
        assertTrue(entityBase.isActivo());
    }

    @Test
    void tipoComidaJpaEntityConDescripcionNulaDeberiaPermitirlo() {
        TipoComidaJpaEntity sinDescripcion = new TipoComidaJpaEntity(
                UUID.randomUUID(), "Menú fijo", null, true,
                LocalDateTime.now(), LocalDateTime.now()
        );
        assertNull(sinDescripcion.getDescripcion());
        assertEquals("Menú fijo", sinDescripcion.getNombre());
    }

    @Test
    void tipoComidaJpaEntityInactivoDeberiaRetornarActivoFalse() {
        TipoComidaJpaEntity inactivo = new TipoComidaJpaEntity(
                tipoComidaId, "Tipo antiguo", "desc", false,
                LocalDateTime.now(), LocalDateTime.now()
        );
        assertFalse(inactivo.isActivo());
    }

    // ── guardar ──────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarTipoComidaYRetornarDominio() {
        when(repository.save(any())).thenReturn(entityBase);

        TipoComida tipoComida = TipoComida.reconstruir(
                tipoComidaId, "Buffet", "Servicio de buffet completo", true
        );
        TipoComida resultado = adapter.guardar(tipoComida);

        assertNotNull(resultado);
        assertEquals(tipoComidaId, resultado.getId());
        assertEquals("Buffet", resultado.getNombre());
        assertEquals("Servicio de buffet completo", resultado.getDescripcion());
        assertTrue(resultado.isActivo());
        verify(repository, times(1)).save(any());
    }

    @Test
    void deberiaGuardarTipoComidaInactivoCorrectamente() {
        TipoComidaJpaEntity entityInactiva = new TipoComidaJpaEntity(
                tipoComidaId, "Tipo antiguo", "desc", false,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.save(any())).thenReturn(entityInactiva);

        TipoComida tipoComida = TipoComida.reconstruir(tipoComidaId, "Tipo antiguo", "desc", false);
        TipoComida resultado = adapter.guardar(tipoComida);

        assertFalse(resultado.isActivo());
    }

    // ── buscarPorId ──────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarPorIdYRetornarPresente() {
        when(repository.findById(tipoComidaId)).thenReturn(Optional.of(entityBase));

        Optional<TipoComida> resultado = adapter.buscarPorId(tipoComidaId);

        assertTrue(resultado.isPresent());
        assertEquals(tipoComidaId, resultado.get().getId());
        assertEquals("Buffet", resultado.get().getNombre());
    }

    @Test
    void deberiaBuscarPorIdYRetornarVacioCuandoNoExiste() {
        when(repository.findById(tipoComidaId)).thenReturn(Optional.empty());

        Optional<TipoComida> resultado = adapter.buscarPorId(tipoComidaId);

        assertFalse(resultado.isPresent());
    }

    // ── listar ───────────────────────────────────────────────────────────────

    @Test
    void deberiaListarTiposComida() {
        TipoComidaJpaEntity otro = new TipoComidaJpaEntity(
                UUID.randomUUID(), "Menú ejecutivo", "Menú de tres tiempos", true,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(entityBase, otro));

        List<TipoComida> resultado = adapter.listar();

        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHayTiposComida() {
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of());

        List<TipoComida> resultado = adapter.listar();

        assertTrue(resultado.isEmpty());
    }

    // ── existeActivoPorId ────────────────────────────────────────────────────

    @Test
    void deberiaRetornarTrueSiExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(tipoComidaId)).thenReturn(true);

        assertTrue(adapter.existeActivoPorId(tipoComidaId));
    }

    @Test
    void deberiaRetornarFalseSiNoExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(tipoComidaId)).thenReturn(false);

        assertFalse(adapter.existeActivoPorId(tipoComidaId));
    }

    // ── existePorNombre ──────────────────────────────────────────────────────

    @Test
    void deberiaRetornarTrueSiExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Buffet")).thenReturn(true);

        assertTrue(adapter.existePorNombre("Buffet"));
    }

    @Test
    void deberiaRetornarFalseSiNoExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Tipo Inexistente")).thenReturn(false);

        assertFalse(adapter.existePorNombre("Tipo Inexistente"));
    }
}