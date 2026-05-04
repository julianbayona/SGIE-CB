package com.ejemplo.monolitomodular.calendario.infraestructura.persistencia;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class EventoCalendarJpaRepositoryAdapter implements EventoCalendarRepository {

    private final SpringDataEventoCalendarJpaRepository repository;

    public EventoCalendarJpaRepositoryAdapter(SpringDataEventoCalendarJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public EventoCalendar guardar(EventoCalendar eventoCalendar) {
        LocalDateTime now = LocalDateTime.now();
        EventoCalendarJpaEntity entity = repository.save(new EventoCalendarJpaEntity(
                eventoCalendar.getId(),
                eventoCalendar.getOrigenTipo(),
                eventoCalendar.getOrigenId(),
                eventoCalendar.getEventoId(),
                eventoCalendar.getTipo(),
                eventoCalendar.getGoogleEventId(),
                eventoCalendar.getFechaSync(),
                eventoCalendar.getEstado(),
                eventoCalendar.getPayloadJson(),
                eventoCalendar.getIntentos(),
                now,
                now
        ));
        return toDomain(entity);
    }

    @Override
    public List<EventoCalendar> buscarPendientes(int limite) {
        return repository.buscarPendientes(PageRequest.of(0, limite)).stream()
                .map(this::toDomain)
                .toList();
    }

    private EventoCalendar toDomain(EventoCalendarJpaEntity entity) {
        return EventoCalendar.reconstruir(
                entity.getId(),
                entity.getOrigenTipo(),
                entity.getOrigenId(),
                entity.getEventoId(),
                entity.getTipo(),
                entity.getGoogleEventId(),
                entity.getFechaSync(),
                entity.getEstado(),
                entity.getPayloadJson(),
                entity.getIntentos()
        );
    }
}
