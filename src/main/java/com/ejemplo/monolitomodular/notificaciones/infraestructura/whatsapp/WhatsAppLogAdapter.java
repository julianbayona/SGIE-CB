package com.ejemplo.monolitomodular.notificaciones.infraestructura.whatsapp;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarWhatsAppCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarWhatsAppResult;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.WhatsAppPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WhatsAppLogAdapter implements WhatsAppPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhatsAppLogAdapter.class);

    @Override
    public EnviarWhatsAppResult enviar(EnviarWhatsAppCommand command) {
        LOGGER.info(
                "Simulando envio WhatsApp. notificacionId={}, telefono={}, payload={}",
                command.notificacionId(),
                command.telefono(),
                command.payloadJson()
        );
        return EnviarWhatsAppResult.ok();
    }
}
