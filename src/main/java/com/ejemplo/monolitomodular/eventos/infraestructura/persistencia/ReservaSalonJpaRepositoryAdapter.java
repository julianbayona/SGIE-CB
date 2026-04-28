package com.ejemplo.monolitomodular.eventos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
                                        reserva.getReservaRaizId(),
                                        reserva.getEventoId(),
                                        reserva.getSalonId(),
                                        reserva.getNumInvitados(),
                                        reserva.getFechaHoraInicio(),
                                        reserva.getFechaHoraFin(),
                                        reserva.getVersion(),
                                        reserva.isVigente(),
                                        reserva.getCreadoPor(),
                                        now,
                                        now
                                ))
                                .toList()
                ).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public ReservaSalon guardar(ReservaSalon reserva) {
        LocalDateTime now = LocalDateTime.now();
        return toDomain(repository.save(new ReservaSalonJpaEntity(
                reserva.getId(),
                reserva.getReservaRaizId(),
                reserva.getEventoId(),
                reserva.getSalonId(),
                reserva.getNumInvitados(),
                reserva.getFechaHoraInicio(),
                reserva.getFechaHoraFin(),
                reserva.getVersion(),
                reserva.isVigente(),
                reserva.getCreadoPor(),
                now,
                now
        )));
    }

    @Override
    public boolean existeConflicto(UUID salonId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
        return repository.existeConflicto(salonId, fechaHoraInicio, fechaHoraFin);
    }

    @Override
    public boolean existeConflicto(UUID salonId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, UUID reservaRaizIdExcluida) {
        return repository.existeConflicto(salonId, fechaHoraInicio, fechaHoraFin, reservaRaizIdExcluida);
    }

    @Override
    public List<ReservaSalon> listarPorEvento(UUID eventoId) {
        return repository.findByEventoIdAndVigenteTrue(eventoId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Set<UUID> buscarSalonesOcupados(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
        return repository.buscarSalonesOcupados(fechaHoraInicio, fechaHoraFin);
    }

    @Override
    public Optional<ReservaSalon> buscarVigentePorEventoYSalon(UUID eventoId, UUID salonId) {
        return repository.findByEventoIdAndSalonIdAndVigenteTrue(eventoId, salonId).map(this::toDomain);
    }

    @Override
    public Optional<ReservaSalon> buscarVigentePorRaizId(UUID reservaRaizId) {
        return repository.findByReservaRaizIdAndVigenteTrue(reservaRaizId).map(this::toDomain);
    }

    @Override
    public void desactivarReservaVigente(UUID reservaRaizId) {
        repository.desactivarReservaVigente(reservaRaizId, LocalDateTime.now());
    }

    private ReservaSalon toDomain(ReservaSalonJpaEntity entity) {
        return ReservaSalon.reconstruir(
                entity.getId(),
                entity.getReservaRaizId(),
                entity.getEventoId(),
                entity.getSalonId(),
                entity.getNumInvitados(),
                entity.getFechaHoraInicio(),
                entity.getFechaHoraFin(),
                entity.getVersion(),
                entity.isVigente(),
                entity.getCreadoPor()
        );
    }
}
