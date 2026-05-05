package com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailResult;

public interface EmailPort {

    EnviarEmailResult enviar(EnviarEmailCommand command);
}
