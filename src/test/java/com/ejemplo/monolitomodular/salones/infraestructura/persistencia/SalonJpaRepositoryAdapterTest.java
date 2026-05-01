package com.ejemplo.monolitomodular.salones.infraestructura.persistencia;

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

@ExtendWith(MockitoExtension.class)
class SalonJpaRepositoryAdapterTest {

    @Mock
    SpringDataSalonJpaRepository repository;

    @InjectMocks
    SalonJpaRepositoryAdapter adapter;

    private UUID salonId;
    private SalonJpaEntity entityBase;

    @BeforeEach
    void setUp() {
        salonId = UUID.randomUUID();
        entityBase = new SalonJpaEntity(
                salonId, "Salon Principal", 100, "Salon grande",
                true, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void deberiaGuardarSalonYRetornarDominio() {
        when(repository.save(any())).thenReturn(entityBase);

        Salon salon = Salon.reconstruir(salonId, "Salon Principal", 100, "Salon grande", true);

        Salon resultado = adapter.guardar(salon);

        assertNotNull(resultado);
        assertEquals(salonId, resultado.getId());
        assertEquals("Salon Principal", resultado.getNombre());
        assertEquals(100, resultado.getCapacidad());
        verify(repository, times(1)).save(any());
    }

    @Test
    void deberiaBuscarPorIdYRetornarPresente() {
        when(repository.findById(salonId)).thenReturn(Optional.of(entityBase));

        Optional<Salon> resultado = adapter.buscarPorId(salonId);

        assertTrue(resultado.isPresent());
        assertEquals(salonId, resultado.get().getId());
    }

    @Test
    void deberiaBuscarPorIdYRetornarVacio() {
        when(repository.findById(salonId)).thenReturn(Optional.empty());

        Optional<Salon> resultado = adapter.buscarPorId(salonId);

        assertFalse(resultado.isPresent());
    }

    @Test
    void deberiaListarSalones() {
        when(repository.findAllByOrderByNombreAsc()).thenReturn(List.of(entityBase));

        List<Salon> resultado = adapter.listar();

        assertEquals(1, resultado.size());
        assertEquals("Salon Principal", resultado.get(0).getNombre());
    }

    @Test
    void deberiaBuscarTodosPorIds() {
        Collection<UUID> ids = Set.of(salonId);
        when(repository.findByIdIn(ids)).thenReturn(List.of(entityBase));

        List<Salon> resultado = adapter.buscarTodosPorIds(ids);

        assertEquals(1, resultado.size());
        assertEquals(salonId, resultado.get(0).getId());
    }

    @Test
    void deberiaVerificarSiExistePorNombre() {
        when(repository.existsByNombreIgnoreCase("Salon Principal")).thenReturn(true);
        when(repository.existsByNombreIgnoreCase("Otro Salon")).thenReturn(false);

        assertTrue(adapter.existePorNombre("Salon Principal"));
        assertFalse(adapter.existePorNombre("Otro Salon"));
    }

    @Test
    void salonJpaEntityDeberiaExponer_getters() {
        assertEquals(salonId, entityBase.getId());
        assertEquals("Salon Principal", entityBase.getNombre());
        assertEquals(100, entityBase.getCapacidad());
        assertEquals("Salon grande", entityBase.getDescripcion());
        assertTrue(entityBase.isActivo());
    }

    @Test
    void deberiaMappearEntidadConDescripcionNulaComoVacio() {
        SalonJpaEntity entitySinDesc = new SalonJpaEntity(
                UUID.randomUUID(), "Salon B", 50, null,
                true, LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.save(any())).thenReturn(entitySinDesc);

        Salon salon = Salon.reconstruir(entitySinDesc.getId(), "Salon B", 50, null, true);
        Salon resultado = adapter.guardar(salon);

        // Salon.reconstruir con descripcion null la convierte a ""
        assertEquals("", resultado.getDescripcion());
    }
}