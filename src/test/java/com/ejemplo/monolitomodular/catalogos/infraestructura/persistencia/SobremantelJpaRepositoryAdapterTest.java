package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Sobremantel;
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
class SobremantelJpaRepositoryAdapterTest {

    @Mock
    SpringDataSobremantelJpaRepository repository;

    @InjectMocks
    SobremantelJpaRepositoryAdapter adapter;

    private UUID sobremantelId;
    private UUID colorId;
    private SobremantelJpaEntity entityBase;

    @BeforeEach
    void setUp() {
        sobremantelId = UUID.randomUUID();
        colorId = UUID.randomUUID();
        entityBase = new SobremantelJpaEntity(
                sobremantelId, "Sobremantel Dorado", colorId, true,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ── SobremantelJpaEntity getters ─────────────────────────────────────────

    @Test
    void sobremantelJpaEntityDeberiaExponerGetters() {
        assertEquals(sobremantelId, entityBase.getId());
        assertEquals("Sobremantel Dorado", entityBase.getNombre());
        assertEquals(colorId, entityBase.getColorId());
        assertTrue(entityBase.isActivo());
    }

    @Test
    void sobremantelJpaEntityInactivoDeberiaRetornarActivoFalse() {
        SobremantelJpaEntity inactivo = new SobremantelJpaEntity(
                sobremantelId, "Sobremantel Rojo", colorId, false,
                LocalDateTime.now(), LocalDateTime.now()
        );
        assertFalse(inactivo.isActivo());
    }

    // ── guardar ──────────────────────────────────────────────────────────────

    @Test
    void deberiaGuardarSobremantelYRetornarDominio() {
        when(repository.save(any())).thenReturn(entityBase);

        Sobremantel sobremantel = Sobremantel.reconstruir(sobremantelId, "Sobremantel Dorado", colorId, true);
        Sobremantel resultado = adapter.guardar(sobremantel);

        assertNotNull(resultado);
        assertEquals(sobremantelId, resultado.getId());
        assertEquals("Sobremantel Dorado", resultado.getNombre());
        assertEquals(colorId, resultado.getColorId());
        assertTrue(resultado.isActivo());
        verify(repository, times(1)).save(any());
    }

    @Test
    void deberiaGuardarSobremantelInactivoCorrectamente() {
        SobremantelJpaEntity entityInactiva = new SobremantelJpaEntity(
                sobremantelId, "Sobremantel Viejo", colorId, false,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.save(any())).thenReturn(entityInactiva);

        Sobremantel sobremantel = Sobremantel.reconstruir(sobremantelId, "Sobremantel Viejo", colorId, false);
        Sobremantel resultado = adapter.guardar(sobremantel);

        assertFalse(resultado.isActivo());
    }

    // ── buscarPorId ──────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarPorIdYRetornarPresente() {
        when(repository.findById(sobremantelId)).thenReturn(Optional.of(entityBase));

        Optional<Sobremantel> resultado = adapter.buscarPorId(sobremantelId);

        assertTrue(resultado.isPresent());
        assertEquals(sobremantelId, resultado.get().getId());
        assertEquals("Sobremantel Dorado", resultado.get().getNombre());
    }

    @Test
    void deberiaBuscarPorIdYRetornarVacioCuandoNoExiste() {
        when(repository.findById(sobremantelId)).thenReturn(Optional.empty());

        Optional<Sobremantel> resultado = adapter.buscarPorId(sobremantelId);

        assertFalse(resultado.isPresent());
    }

    // ── listar ───────────────────────────────────────────────────────────────

    @Test
    void deberiaListarSobremanteles() {
        SobremantelJpaEntity otro = new SobremantelJpaEntity(
                UUID.randomUUID(), "Sobremantel Plateado", colorId, true,
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(entityBase, otro));

        List<Sobremantel> resultado = adapter.listar();

        assertEquals(2, resultado.size());
    }

    @Test
    void deberiaRetornarListaVaciaSiNoHaySobremanteles() {
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of());

        List<Sobremantel> resultado = adapter.listar();

        assertTrue(resultado.isEmpty());
    }

    // ── existeActivoPorId ────────────────────────────────────────────────────

    @Test
    void deberiaRetornarTrueSiExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(sobremantelId)).thenReturn(true);

        assertTrue(adapter.existeActivoPorId(sobremantelId));
    }

    @Test
    void deberiaRetornarFalseSiNoExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(sobremantelId)).thenReturn(false);

        assertFalse(adapter.existeActivoPorId(sobremantelId));
    }

    // ── existePorNombre ──────────────────────────────────────────────────────

    @Test
    void deberiaRetornarTrueSiExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Sobremantel Dorado")).thenReturn(true);

        assertTrue(adapter.existePorNombre("Sobremantel Dorado"));
    }

    @Test
    void deberiaRetornarFalseSiNoExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Sobremantel Inexistente")).thenReturn(false);

        assertFalse(adapter.existePorNombre("Sobremantel Inexistente"));
    }
}