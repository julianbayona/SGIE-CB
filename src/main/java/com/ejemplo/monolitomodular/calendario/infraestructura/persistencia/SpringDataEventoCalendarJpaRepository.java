package com.ejemplo.monolitomodular.calendario.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

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
}
