package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Mantel;
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
class MantelJpaRepositoryAdapterTest {

    @Mock
    SpringDataMantelJpaRepository repository;

    @InjectMocks
    MantelJpaRepositoryAdapter adapter;

    private UUID mantelId;
    private UUID colorId;
    private MantelJpaEntity entityBase;

    @BeforeEach
    void setUp() {
        mantelId = UUID.randomUUID();
        colorId = UUID.randomUUID();
        entityBase = new MantelJpaEntity(
                mantelId, "Mantel Blanco", colorId, true,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ── MantelJpaEntity getters ──────────────────────────────────────────────

    @Test
    void mantelJpaEntityDeberiaExponerGetters() {
        assertEquals(mantelId, entityBase.getId());
        assertEquals("Mantel Blanco", entityBase.getNombre());
        assertEquals(colorId, entityBase.getColorId());
        assertTrue(entityBase.isActivo());
    }

    @Test
    void mantelJpaEntityInactivoDeberiaRetornarActivoFalse() {
        MantelJpaEntity inactivo = new MantelJpaEntity(
                mantelId, "Mantel Rojo", colorId, false,
                LocalDateTime.now(), LocalDateTime.now()
        );
        assertFalse(inactivo.isActivo());
    }

    // ── guardar ──────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarMantelYRetornarDominio() {
        when(repository.save(any())).thenReturn(entityBase);

        Mantel mantel = Mantel.reconstruir(mantelId, "Mantel Blanco", colorId, true);
        Mantel resultado = adapter.guardar(mantel);

        assertNotNull(resultado);
        assertEquals(mantelId, resultado.getId());
        assertEquals("Mantel Blanco", resultado.getNombre());
        assertEquals(colorId, resultado.getColorId());
        assertTrue(resultado.isActivo());
        verify(repository, times(1)).save(any());
    }

    @Test
    void deberiaGuardarMantelInactivoCorrectamente() {
        MantelJpaEntity entityInactiva = new MantelJpaEntity(
                mantelId, "Mantel Viejo", colorId, false,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.save(any())).thenReturn(entityInactiva);

        Mantel mantel = Mantel.reconstruir(mantelId, "Mantel Viejo", colorId, false);
        Mantel resultado = adapter.guardar(mantel);

        assertFalse(resultado.isActivo());
    }

    // ── buscarPorId ──────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarPorIdYRetornarPresente() {
        when(repository.findById(mantelId)).thenReturn(Optional.of(entityBase));

        Optional<Mantel> resultado = adapter.buscarPorId(mantelId);

        assertTrue(resultado.isPresent());
        assertEquals(mantelId, resultado.get().getId());
        assertEquals("Mantel Blanco", resultado.get().getNombre());
    }

    @Test
    void deberiaBuscarPorIdYRetornarVacioCuandoNoExiste() {
        when(repository.findById(mantelId)).thenReturn(Optional.empty());

        Optional<Mantel> resultado = adapter.buscarPorId(mantelId);

        assertFalse(resultado.isPresent());
    }

    // ── listar ───────────────────────────────────────────────────────────────

    @Test
    void deberiaListarManteles() {
        MantelJpaEntity otro = new MantelJpaEntity(
                UUID.randomUUID(), "Mantel Azul", colorId, true,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(entityBase, otro));

        List<Mantel> resultado = adapter.listar();

        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHayManteles() {
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of());

        List<Mantel> resultado = adapter.listar();

        assertTrue(resultado.isEmpty());
    }

    // ── existeActivoPorId ────────────────────────────────────────────────────

    @Test
    void deberiaRetornarTrueSiExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(mantelId)).thenReturn(true);

        assertTrue(adapter.existeActivoPorId(mantelId));
    }

    @Test
    void deberiaRetornarFalseSiNoExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(mantelId)).thenReturn(false);

        assertFalse(adapter.existeActivoPorId(mantelId));
    }

    // ── existePorNombre ──────────────────────────────────────────────────────

    @Test
    void deberiaRetornarTrueSiExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Mantel Blanco")).thenReturn(true);

        assertTrue(adapter.existePorNombre("Mantel Blanco"));
    }

    @Test
    void deberiaRetornarFalseSiNoExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Mantel Inexistente")).thenReturn(false);

        assertFalse(adapter.existePorNombre("Mantel Inexistente"));
    }
}