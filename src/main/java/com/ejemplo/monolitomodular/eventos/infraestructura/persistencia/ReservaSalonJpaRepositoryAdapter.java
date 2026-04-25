package com.ejemplo.monolitomodular.eventos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class ReservaSalonJpaRepositoryAdapter implements ReservaSalonRepository {

    private final SpringDataReservaSalonJpaRepository repository;

    public ReservaSalonJpaRepositoryAdapter(SpringDataReservaSalonJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ReservaSalon> guardarTodas(List<ReservaSalon> reservas) {
        LocalDateTime now = LocalDateTime.now();
        return repository.saveAll(
                        reservas.stream()
                                .map(reserva -> new ReservaSalonJpaEntity(
                                        reserva.getId(),
                                        reserva.getEventoId(),
                                        reserva.getSalonId(),
                                        reserva.getFechaInicio(),
                                        reserva.getFechaFin(),
                                        now
                                ))
                                .toList()
                ).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existeConflicto(UUID salonId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return repository.existeConflicto(salonId, fechaInicio, fechaFin);
    }

    @Override
    public List<ReservaSalon> listarPorEvento(UUID eventoId) {
        return repository.findByEventoId(eventoId).stream()
                .map(this::toDomain)
                .toList();
    }

    private ReservaSalon toDomain(ReservaSalonJpaEntity entity) {
        return ReservaSalon.reconstruir(
                entity.getId(),
                entity.getEventoId(),
                entity.getSalonId(),
                entity.getFechaInicio(),
                entity.getFechaFin()
        );
    }
}
