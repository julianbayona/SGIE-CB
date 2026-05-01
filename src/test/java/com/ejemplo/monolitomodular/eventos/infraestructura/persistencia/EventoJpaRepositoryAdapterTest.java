package com.ejemplo.monolitomodular.eventos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
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
class EventoJpaRepositoryAdapterTest {

    @Mock
    SpringDataEventoJpaRepository repository;

    @InjectMocks
    EventoJpaRepositoryAdapter adapter;

    private UUID eventoId;
    private EventoJpaEntity entityBase;

    private static final LocalDateTime INICIO = LocalDateTime.of(2026, 6, 15, 18, 0);
    private static final LocalDateTime FIN    = LocalDateTime.of(2026, 6, 15, 22, 0);

    @BeforeEach
    void setUp() {
        eventoId = UUID.randomUUID();
        entityBase = new EventoJpaEntity(
                eventoId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                INICIO, FIN,
                EstadoEvento.PENDIENTE,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void deberiaGuardarEventoYRetornarDominio() {
        when(repository.save(any())).thenReturn(entityBase);

        Evento evento = Evento.reconstruir(
                entityBase.getId(),
                entityBase.getClienteId(),
                entityBase.getTipoEventoId(),
                entityBase.getTipoComidaId(),
                entityBase.getUsuarioCreadorId(),
                INICIO, FIN,
                EstadoEvento.PENDIENTE,
                null
        );

        Evento resultado = adapter.guardar(evento);

        assertNotNull(resultado);
        assertEquals(eventoId, resultado.getId());
        assertEquals(EstadoEvento.PENDIENTE, resultado.getEstado());
        verify(repository, times(1)).save(any());
    }

    @Test
    void deberiaBuscarPorIdYRetornarPresente() {
        when(repository.findById(eventoId)).thenReturn(Optional.of(entityBase));

        Optional<Evento> resultado = adapter.buscarPorId(eventoId);

        assertTrue(resultado.isPresent());
        assertEquals(eventoId, resultado.get().getId());
        verify(repository).findById(eventoId);
    }

    @Test
    void deberiaBuscarPorIdYRetornarVacioSiNoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        Optional<Evento> resultado = adapter.buscarPorId(idInexistente);

        assertFalse(resultado.isPresent());
        verify(repository).findById(idInexistente);
    }

    @Test
    void deberiaListarEventosOrdenados() {
        EventoJpaEntity entity2 = new EventoJpaEntity(
                UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                INICIO.plusDays(1), FIN.plusDays(1),
                EstadoEvento.CONFIRMADO, "gcal-123",
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(entityBase, entity2));

        List<Evento> resultado = adapter.listar();

        assertEquals(2, resultado.size());
        assertEquals(eventoId, resultado.get(0).getId());
        assertEquals(EstadoEvento.CONFIRMADO, resultado.get(1).getEstado());
        verify(repository).findAllByOrderByCreatedAtAsc();
    }

    @Test
    void deberiaListarEventosVacioSiNoHay() {
        when(repository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of());

        List<Evento> resultado = adapter.listar();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deberiaConservarGcalEventIdEnMapeo() {
        EventoJpaEntity conGcal = new EventoJpaEntity(
                UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                INICIO, FIN,
                EstadoEvento.COTIZACION_ENVIADA, "gcal-abc-123",
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(repository.save(any())).thenReturn(conGcal);

        Evento evento = Evento.reconstruir(
                conGcal.getId(), conGcal.getClienteId(), conGcal.getTipoEventoId(),
                conGcal.getTipoComidaId(), conGcal.getUsuarioCreadorId(),
                INICIO, FIN, EstadoEvento.COTIZACION_ENVIADA, "gcal-abc-123"
        );

        Evento resultado = adapter.guardar(evento);

        assertEquals("gcal-abc-123", resultado.getGcalEventId());
    }
}