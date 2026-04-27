package com.ejemplo.monolitomodular.eventos.infraestructura.persistencia;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SpringDataReservaSalonJpaRepository extends JpaRepository<ReservaSalonJpaEntity, UUID> {

    @Query("""
            select case when count(r) > 0 then true else false end
            from ReservaSalonJpaEntity r
            where r.salonId = :salonId
              and r.vigente = true
              and upper(r.estado) = 'CONFIRMADO'
              and r.fechaHoraInicio < :fechaHoraFin
              and r.fechaHoraFin > :fechaHoraInicio
            """)
    boolean existeConflicto(UUID salonId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin);

    @Query("""
            select case when count(r) > 0 then true else false end
            from ReservaSalonJpaEntity r
            where r.salonId = :salonId
              and r.vigente = true
              and r.reservaRaizId <> :reservaRaizIdExcluida
              and upper(r.estado) = 'CONFIRMADO'
              and r.fechaHoraInicio < :fechaHoraFin
              and r.fechaHoraFin > :fechaHoraInicio
            """)
    boolean existeConflicto(UUID salonId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, UUID reservaRaizIdExcluida);

    List<ReservaSalonJpaEntity> findByEventoIdAndVigenteTrue(UUID eventoId);

    Optional<ReservaSalonJpaEntity> findByEventoIdAndSalonIdAndVigenteTrue(UUID eventoId, UUID salonId);

    Optional<ReservaSalonJpaEntity> findByReservaRaizIdAndVigenteTrue(UUID reservaRaizId);

    @Query("""
            select distinct r.salonId
            from ReservaSalonJpaEntity r
            where r.vigente = true
              and upper(r.estado) = 'CONFIRMADO'
              and r.fechaHoraInicio < :fechaHoraFin
              and r.fechaHoraFin > :fechaHoraInicio
            """)
    Set<UUID> buscarSalonesOcupados(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin);

    @Modifying
    @Query("""
            update ReservaSalonJpaEntity r
               set r.vigente = false,
                   r.updatedAt = :updatedAt
             where r.reservaRaizId = :reservaRaizId
               and r.vigente = true
            """)
    int desactivarReservaVigente(UUID reservaRaizId, LocalDateTime updatedAt);
}
