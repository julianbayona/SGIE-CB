package com.ejemplo.monolitomodular.notificaciones.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataNotificacionDestinatarioJpaRepository extends JpaRepository<NotificacionDestinatarioJpaEntity, UUID> {

    List<NotificacionDestinatarioJpaEntity> findByNotificacionId(UUID notificacionId);

    void deleteByNotificacionId(UUID notificacionId);
}
