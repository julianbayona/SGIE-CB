package com.ejemplo.monolitomodular.notificaciones.infraestructura.persistencia;

import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.Notificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.NotificacionDestinatario;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.NotificacionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class NotificacionJpaRepositoryAdapter implements NotificacionRepository {

    private final SpringDataNotificacionJpaRepository notificacionRepository;
    private final SpringDataNotificacionDestinatarioJpaRepository destinatarioRepository;

    public NotificacionJpaRepositoryAdapter(
            SpringDataNotificacionJpaRepository notificacionRepository,
            SpringDataNotificacionDestinatarioJpaRepository destinatarioRepository
    ) {
        this.notificacionRepository = notificacionRepository;
        this.destinatarioRepository = destinatarioRepository;
    }

    @Override
    public Notificacion guardar(Notificacion notificacion) {
        LocalDateTime now = LocalDateTime.now();
        notificacionRepository.save(new NotificacionJpaEntity(
                notificacion.getId(),
                notificacion.getEventoId(),
                notificacion.getTipoNotificacionId(),
                notificacion.getFechaProgramada(),
                notificacion.getFechaEnvio(),
                notificacion.getEstado(),
                notificacion.getIntentos(),
                notificacion.getPayloadJson(),
                now,
                now
        ));
        destinatarioRepository.saveAll(notificacion.getDestinatarios().stream().map(this::toEntity).toList());
        return toDomain(notificacionRepository.findById(notificacion.getId()).orElseThrow());
    }

    @Override
    public List<Notificacion> buscarPendientes(LocalDateTime fechaReferencia, int limite) {
        return notificacionRepository.buscarPendientes(fechaReferencia, PageRequest.of(0, limite)).stream()
                .map(this::toDomain)
                .toList();
    }

    private Notificacion toDomain(NotificacionJpaEntity entity) {
        List<NotificacionDestinatario> destinatarios = destinatarioRepository.findByNotificacionId(entity.getId()).stream()
                .map(this::toDomain)
                .toList();
        return Notificacion.reconstruir(
                entity.getId(),
                entity.getEventoId(),
                entity.getTipoNotificacionId(),
                entity.getFechaProgramada(),
                entity.getFechaEnvio(),
                entity.getEstado(),
                entity.getIntentos(),
                entity.getPayloadJson(),
                destinatarios
        );
    }

    private NotificacionDestinatario toDomain(NotificacionDestinatarioJpaEntity entity) {
        return NotificacionDestinatario.reconstruir(
                entity.getId(),
                entity.getNotificacionId(),
                entity.getUsuarioId(),
                entity.getTelefono(),
                entity.getEstado()
        );
    }

    private NotificacionDestinatarioJpaEntity toEntity(NotificacionDestinatario destinatario) {
        return new NotificacionDestinatarioJpaEntity(
                destinatario.getId(),
                destinatario.getNotificacionId(),
                destinatario.getUsuarioId(),
                destinatario.getTelefono(),
                destinatario.getEstado()
        );
    }
}
