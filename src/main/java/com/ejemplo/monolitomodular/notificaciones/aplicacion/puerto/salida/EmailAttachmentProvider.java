package com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.salida;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailCommand;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;

import java.util.List;

public interface EmailAttachmentProvider {

    List<EnviarEmailCommand.Adjunto> adjuntos(TipoNotificacion tipo, String payloadJson);
}
