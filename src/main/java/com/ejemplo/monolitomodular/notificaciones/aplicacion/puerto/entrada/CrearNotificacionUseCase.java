package com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.NotificacionView;

public interface CrearNotificacionUseCase {

    NotificacionView ejecutar(CrearNotificacionCommand command);
}
