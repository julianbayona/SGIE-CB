package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import com.ejemplo.monolitomodular.menus.dominio.modelo.Plato;
import com.ejemplo.monolitomodular.menus.dominio.modelo.TipoMomentoMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatoYTipoMomentoMenuAdapterTest {

    // ─── PlatoJpaRepositoryAdapter ───────────────────────────────────────────

    @Mock
    SpringDataPlatoJpaRepository platoRepository;

    @Mock
    SpringDataPlatoMomentoJpaRepository platoMomentoRepository;

    @InjectMocks
    PlatoJpaRepositoryAdapter platoAdapter;

    // ─── TipoMomentoMenuJpaRepositoryAdapter ─────────────────────────────────

    @Mock
    SpringDataTipoMomentoMenuJpaRepository tipoMomentoMenuRepository;

    @InjectMocks
    TipoMomentoMenuJpaRepositoryAdapter tipoMomentoMenuAdapter;

    private UUID platoId;
    private UUID tipoMomentoId;
    private PlatoJpaEntity platoEntity;
    private TipoMomentoMenuJpaEntity tipoMomentoMenuEntity;

    @BeforeEach
    void setUp() {
        platoId = UUID.randomUUID();
        tipoMomentoId = UUID.randomUUID();
    }

    // ─── Plato tests ─────────────────────────────────────────────────────────

    @Test
    void deberiaBuscarPlatoPorIdYRetornarPresente() {
        platoEntity = crearPlatoEntity(platoId, "Pollo asado", "Descripcion", new BigDecimal("25000"), true);
        when(platoRepository.findById(platoId)).thenReturn(Optional.of(platoEntity));

        Optional<Plato> resultado = platoAdapter.buscarPorId(platoId);

        assertTrue(resultado.isPresent());
        assertEquals(platoId, resultado.get().getId());
        assertEquals("Pollo asado", resultado.get().getNombre());
        assertTrue(resultado.get().isActivo());
    }

    @Test
    void deberiaBuscarPlatoPorIdYRetornarVacio() {
        when(platoRepository.findById(platoId)).thenReturn(Optional.empty());

        Optional<Plato> resultado = platoAdapter.buscarPorId(platoId);

        assertFalse(resultado.isPresent());
    }

    @Test
    void deberiaVerificarQueExistePlatoActivoParaMomento() {
        when(platoMomentoRepository.existsActivoByPlatoIdAndTipoMomentoId(platoId, tipoMomentoId)).thenReturn(true);

        assertTrue(platoAdapter.existeActivoParaMomento(platoId, tipoMomentoId));
        verify(platoMomentoRepository).existsActivoByPlatoIdAndTipoMomentoId(platoId, tipoMomentoId);
    }

    @Test
    void deberiaRetornarFalseSiPlatoNoEsActivoParaMomento() {
        when(platoMomentoRepository.existsActivoByPlatoIdAndTipoMomentoId(platoId, tipoMomentoId)).thenReturn(false);

        assertFalse(platoAdapter.existeActivoParaMomento(platoId, tipoMomentoId));
    }

    @Test
    void deberiaMappearPlatoConDescripcionNula() {
        platoEntity = crearPlatoEntity(platoId, "Ensalada", null, new BigDecimal("15000"), true);
        when(platoRepository.findById(platoId)).thenReturn(Optional.of(platoEntity));

        Optional<Plato> resultado = platoAdapter.buscarPorId(platoId);

        assertTrue(resultado.isPresent());
        assertNull(resultado.get().getDescripcion());
    }

    @Test
    void deberiaMappearPlatoInactivo() {
        platoEntity = crearPlatoEntity(platoId, "Sopa", "Desc", new BigDecimal("10000"), false);
        when(platoRepository.findById(platoId)).thenReturn(Optional.of(platoEntity));

        Optional<Plato> resultado = platoAdapter.buscarPorId(platoId);

        assertTrue(resultado.isPresent());
        assertFalse(resultado.get().isActivo());
    }

    // ─── TipoMomentoMenu tests ───────────────────────────────────────────────

    @Test
    void deberiaBuscarTipoMomentoMenuPorIdYRetornarPresente() {
        tipoMomentoMenuEntity = crearTipoMomentoEntity(tipoMomentoId, "Almuerzo", true);
        when(tipoMomentoMenuRepository.findById(tipoMomentoId)).thenReturn(Optional.of(tipoMomentoMenuEntity));

        Optional<TipoMomentoMenu> resultado = tipoMomentoMenuAdapter.buscarPorId(tipoMomentoId);

        assertTrue(resultado.isPresent());
        assertEquals(tipoMomentoId, resultado.get().getId());
        assertEquals("Almuerzo", resultado.get().getNombre());
        assertTrue(resultado.get().isActivo());
    }

    @Test
    void deberiaBuscarTipoMomentoMenuPorIdYRetornarVacio() {
        when(tipoMomentoMenuRepository.findById(tipoMomentoId)).thenReturn(Optional.empty());

        Optional<TipoMomentoMenu> resultado = tipoMomentoMenuAdapter.buscarPorId(tipoMomentoId);

        assertFalse(resultado.isPresent());
    }

    @Test
    void deberiaVerificarQueExisteTipoMomentoMenuActivoPorId() {
        when(tipoMomentoMenuRepository.existsByIdAndActivoTrue(tipoMomentoId)).thenReturn(true);

        assertTrue(tipoMomentoMenuAdapter.existeActivoPorId(tipoMomentoId));
        verify(tipoMomentoMenuRepository).existsByIdAndActivoTrue(tipoMomentoId);
    }

    @Test
    void deberiaRetornarFalseSiTipoMomentoMenuNoEsActivo() {
        when(tipoMomentoMenuRepository.existsByIdAndActivoTrue(tipoMomentoId)).thenReturn(false);

        assertFalse(tipoMomentoMenuAdapter.existeActivoPorId(tipoMomentoId));
    }

    @Test
    void deberiaMappearTipoMomentoMenuInactivo() {
        tipoMomentoMenuEntity = crearTipoMomentoEntity(tipoMomentoId, "Cena", false);
        when(tipoMomentoMenuRepository.findById(tipoMomentoId)).thenReturn(Optional.of(tipoMomentoMenuEntity));

        Optional<TipoMomentoMenu> resultado = tipoMomentoMenuAdapter.buscarPorId(tipoMomentoId);

        assertTrue(resultado.isPresent());
        assertFalse(resultado.get().isActivo());
    }

    @Test
    void platoMomentoJpaIdDeberiaImplementarEqualsYHashCode() {
        UUID p = UUID.randomUUID();
        UUID t = UUID.randomUUID();
        PlatoMomentoJpaId id1 = new PlatoMomentoJpaId(p, t);
        PlatoMomentoJpaId id2 = new PlatoMomentoJpaId(p, t);
        PlatoMomentoJpaId id3 = new PlatoMomentoJpaId(UUID.randomUUID(), t);

        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }

    @Test
    void platoMomentoJpaIdSinArgumentosDeberiaCrearseCorrectamente() {
        PlatoMomentoJpaId id = new PlatoMomentoJpaId();
        assertNotNull(id);
    }

    // ─── helpers ─────────────────────────────────────────────────────────────

    private PlatoJpaEntity crearPlatoEntity(UUID id, String nombre, String descripcion,
                                             BigDecimal precioBase, boolean activo) {
        // Usamos reflexión para crear la entidad ya que el constructor protegido no es accesible directamente
        try {
            var constructor = PlatoJpaEntity.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            PlatoJpaEntity entity = constructor.newInstance();
            setField(entity, "id", id);
            setField(entity, "nombre", nombre);
            setField(entity, "descripcion", descripcion);
            setField(entity, "precioBase", precioBase);
            setField(entity, "activo", activo);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear PlatoJpaEntity", e);
        }
    }

    private TipoMomentoMenuJpaEntity crearTipoMomentoEntity(UUID id, String nombre, boolean activo) {
        try {
            var constructor = TipoMomentoMenuJpaEntity.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            TipoMomentoMenuJpaEntity entity = constructor.newInstance();
            setField(entity, "id", id);
            setField(entity, "nombre", nombre);
            setField(entity, "activo", activo);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear TipoMomentoMenuJpaEntity", e);
        }
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}