package com.ejemplo.monolitomodular.calendario.infraestructura.persistencia;

import com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.OrigenEventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataEventoCalendarJpaRepository extends JpaRepository<EventoCalendarJpaEntity, UUID> {

    @Query("""
            select e
            from EventoCalendarJpaEntity e
            where e.intentos < 3
              and e.estado in (
                  com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar.PENDIENTE,
                  com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar.ERROR
              )
            order by e.createdAt asc
            """)
    List<EventoCalendarJpaEntity> buscarPendientes(Pageable pageable);

    List<EventoCalendarJpaEntity> findByEventoIdAndEstadoAndTipoIn(
            UUID eventoId,
            EstadoEventoCalendar estado,
            List<TipoOperacionCalendar> tipos
    );

    List<EventoCalendarJpaEntity> findByEventoIdOrderByCreatedAtDesc(UUID eventoId);

    @Modifying
    @Query("""
            update EventoCalendarJpaEntity e
               set e.estado = com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar.CANCELADO,
                   e.updatedAt = :updatedAt
             where e.eventoId = :eventoId
               and e.estado in (
                   com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar.PENDIENTE,
                   com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar.ERROR
               )
            """)
    int cancelarPendientesPorEventoId(UUID eventoId, LocalDateTime updatedAt);

    @Modifying
    @Query("""
            update EventoCalendarJpaEntity e
               set e.estado = com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar.CANCELADO,
                   e.updatedAt = :updatedAt
             where e.eventoId = :eventoId
               and e.origenTipo = :origenTipo
               and e.estado in (
                   com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar.PENDIENTE,
                   com.ejemplo.monolitomodular.calendario.dominio.modelo.EstadoEventoCalendar.ERROR
               )
            """)
    int cancelarPendientesPorEventoYOrigen(UUID eventoId, OrigenEventoCalendar origenTipo, LocalDateTime updatedAt);
}
