package com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.NotificacionDetalleView;

import java.util.List;
import java.util.UUID;

public interface ListarNotificacionesPorEventoUseCase {

    List<NotificacionDetalleView> listarPorEvento(UUID eventoId);
}
