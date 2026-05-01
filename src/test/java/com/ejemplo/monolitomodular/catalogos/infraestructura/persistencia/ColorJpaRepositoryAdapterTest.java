package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Color;
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
class ColorJpaRepositoryAdapterTest {

    @Mock
    SpringDataColorJpaRepository repository;

    @InjectMocks
    ColorJpaRepositoryAdapter adapter;

    private UUID colorId;
    private ColorJpaEntity entityBase;

    @BeforeEach
    void setUp() {
        colorId = UUID.randomUUID();
        entityBase = new ColorJpaEntity(
                colorId, "Rojo", "#FF0000", true,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void deberiaGuardarColorYRetornarDominio() {
        when(repository.save(any())).thenReturn(entityBase);

        Color color = Color.reconstruir(colorId, "Rojo", "#FF0000", true);
        Color resultado = adapter.guardar(color);

        assertNotNull(resultado);
        assertEquals(colorId, resultado.getId());
        assertEquals("Rojo", resultado.getNombre());
        assertEquals("#FF0000", resultado.getCodigoHex());
        assertTrue(resultado.isActivo());
        verify(repository, times(1)).save(any());
    }

    @Test
    void deberiaBuscarPorIdYRetornarPresente() {
        when(repository.findById(colorId)).thenReturn(Optional.of(entityBase));

        Optional<Color> resultado = adapter.buscarPorId(colorId);

        assertTrue(resultado.isPresent());
        assertEquals(colorId, resultado.get().getId());
    }

    @Test
    void deberiaBuscarPorIdYRetornarVacio() {
        when(repository.findById(colorId)).thenReturn(Optional.empty());

        Optional<Color> resultado = adapter.buscarPorId(colorId);

        assertFalse(resultado.isPresent());
    }

    @Test
    void deberiaListarColores() {
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(entityBase));

        List<Color> resultado = adapter.listar();

        assertEquals(1, resultado.size());
        assertEquals("Rojo", resultado.get(0).getNombre());
    }

    @Test
    void deberiaVerificarSiExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(colorId)).thenReturn(true);

        assertTrue(adapter.existeActivoPorId(colorId));
    }

    @Test
    void deberiaRetornarFalseSiNoExisteActivoPorId() {
        when(repository.existsByIdAndActivoTrue(colorId)).thenReturn(false);

        assertFalse(adapter.existeActivoPorId(colorId));
    }

    @Test
    void deberiaVerificarSiExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Rojo")).thenReturn(true);
        when(repository.existsByNombreIgnoreCase("Azul")).thenReturn(false);

        assertTrue(adapter.existePorNombre("Rojo"));
        assertFalse(adapter.existePorNombre("Azul"));
    }

    @Test
    void colorJpaEntityDeberiaExponer_getters() {
        assertEquals(colorId, entityBase.getId());
        assertEquals("Rojo", entityBase.getNombre());
        assertEquals("#FF0000", entityBase.getCodigoHex());
        assertTrue(entityBase.isActivo());
    }
}