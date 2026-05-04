package com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida;

import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.Notificacion;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificacionRepository {

    Notificacion guardar(Notificacion notificacion);

    List<Notificacion> buscarPendientes(LocalDateTime fechaReferencia, int limite);
}
