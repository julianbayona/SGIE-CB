package com.ejemplo.monolitomodular.eventos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EventoJpaRepositoryAdapter implements EventoRepository {

    private final SpringDataEventoJpaRepository repository;

    public EventoJpaRepositoryAdapter(SpringDataEventoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Evento guardar(Evento evento) {
        LocalDateTime now = LocalDateTime.now();
        EventoJpaEntity entity = new EventoJpaEntity(
                evento.getId(),
                evento.getClienteId(),
                evento.getTipoEventoId(),
                evento.getTipoComidaId(),
                evento.getUsuarioCreadorId(),
                evento.getFechaHoraInicio(),
                evento.getFechaHoraFin(),
                evento.getEstado(),
                evento.getGcalEventId(),
                now,
                now
        );
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<Evento> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Evento> listar() {
        return repository.findAllByOrderByCreatedAtAsc().stream()
                .map(this::toDomain)
                .toList();
    }

    private Evento toDomain(EventoJpaEntity entity) {
        return Evento.reconstruir(
                entity.getId(),
                entity.getClienteId(),
                entity.getTipoEventoId(),
                entity.getTipoComidaId(),
                entity.getUsuarioCreadorId(),
                entity.getFechaHoraInicio(),
                entity.getFechaHoraFin(),
                entity.getEstado(),
                entity.getGcalEventId()
        );
    }
}
