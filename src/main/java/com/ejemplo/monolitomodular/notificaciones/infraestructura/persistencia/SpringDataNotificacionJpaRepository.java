package com.ejemplo.monolitomodular.notificaciones.infraestructura.persistencia;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataNotificacionJpaRepository extends JpaRepository<NotificacionJpaEntity, UUID> {

    @Query("""
            select n
            from NotificacionJpaEntity n
            where n.fechaProgramada <= :fechaReferencia
              and n.intentos < 3
              and n.estado in (
                  com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoNotificacion.PENDIENTE,
                  com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoNotificacion.ERROR
              )
            order by n.fechaProgramada asc
            """)
    List<NotificacionJpaEntity> buscarPendientes(LocalDateTime fechaReferencia, Pageable pageable);
}
