package com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarWhatsAppCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarWhatsAppResult;

public interface WhatsAppPort {

    EnviarWhatsAppResult enviar(EnviarWhatsAppCommand command);
}
