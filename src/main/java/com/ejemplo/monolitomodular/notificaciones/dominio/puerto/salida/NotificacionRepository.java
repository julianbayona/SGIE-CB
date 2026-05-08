package com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida;

import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.Notificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificacionRepository {

    Notificacion guardar(Notificacion notificacion);

    List<Notificacion> buscarPendientes(LocalDateTime fechaReferencia, int limite);

    boolean existePorEventoYTipoDesde(UUID eventoId, TipoNotificacion tipo, LocalDateTime fechaDesde);

    default List<Notificacion> buscarPorEventoId(UUID eventoId) {
        return List.of();
    }

    default List<Notificacion> buscarCancelablesPorEventoYTipo(UUID eventoId, TipoNotificacion tipo) {
        return List.of();
    }

    default List<Notificacion> buscarCancelablesPorEventoId(UUID eventoId) {
        return List.of();
    }
}
