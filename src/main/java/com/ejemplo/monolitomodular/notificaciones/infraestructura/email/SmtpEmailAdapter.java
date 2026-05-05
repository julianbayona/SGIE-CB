package com.ejemplo.monolitomodular.notificaciones.infraestructura.email;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailResult;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.EmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "sgie.notificaciones.email", name = "enabled", havingValue = "true")
public class SmtpEmailAdapter implements EmailPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpEmailAdapter.class);

    private final JavaMailSender mailSender;
    private final String from;

    public SmtpEmailAdapter(
            JavaMailSender mailSender,
            @Value("${sgie.notificaciones.email.from:}") String from
    ) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public EnviarEmailResult enviar(EnviarEmailCommand command) {
        if (from == null || from.isBlank()) {
            return EnviarEmailResult.error("No esta configurado sgie.notificaciones.email.from");
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from.trim());
            message.setTo(command.correo());
            message.setSubject(command.asunto());
            message.setText(command.cuerpo());
            mailSender.send(message);
            return EnviarEmailResult.ok();
        } catch (RuntimeException ex) {
            LOGGER.error(
                    "Fallo enviando Email. notificacionId={}, correo={}",
                    command.notificacionId(),
                    command.correo(),
                    ex
            );
            return EnviarEmailResult.error(ex.getMessage());
        }
    }
}
