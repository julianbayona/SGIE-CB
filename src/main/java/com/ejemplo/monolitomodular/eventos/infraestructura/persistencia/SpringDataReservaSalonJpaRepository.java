package com.ejemplo.monolitomodular.eventos.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataReservaSalonJpaRepository extends JpaRepository<ReservaSalonJpaEntity, UUID> {

    @Query("""
            select case when count(r) > 0 then true else false end
            from ReservaSalonJpaEntity r, EventoJpaEntity e
            where r.eventoId = e.id
              and r.salonId = :salonId
              and e.estado <> com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento.CANCELADO
              and r.fechaInicio < :fechaFin
              and r.fechaFin > :fechaInicio
            """)
    boolean existeConflicto(UUID salonId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<ReservaSalonJpaEntity> findByEventoId(UUID eventoId);
}
