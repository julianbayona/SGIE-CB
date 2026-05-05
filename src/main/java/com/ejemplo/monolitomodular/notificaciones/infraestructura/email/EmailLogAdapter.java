package com.ejemplo.monolitomodular.notificaciones.infraestructura.email;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailResult;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.EmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "sgie.notificaciones.email", name = "enabled", havingValue = "false", matchIfMissing = true)
public class EmailLogAdapter implements EmailPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailLogAdapter.class);

    @Override
    public EnviarEmailResult enviar(EnviarEmailCommand command) {
        LOGGER.info(
                "Simulando envio Email. notificacionId={}, correo={}, tipo={}, asunto={}, cuerpo={}",
                command.notificacionId(),
                command.correo(),
                command.tipo(),
                command.asunto(),
                command.cuerpo()
        );
        return EnviarEmailResult.ok();
    }
}
