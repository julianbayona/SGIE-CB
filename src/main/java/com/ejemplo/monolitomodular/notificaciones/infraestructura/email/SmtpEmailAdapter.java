package com.ejemplo.monolitomodular.notificaciones.infraestructura.email;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailResult;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.EmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("'${sgie.notificaciones.email.enabled:false}' == 'true' && '${sgie.notificaciones.email.provider:smtp}' == 'smtp'")
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
            mailSender.send(mimeMessage -> {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setFrom(from.trim());
                helper.setTo(command.correo());
                helper.setSubject(command.asunto());
                helper.setText(command.cuerpo(), false);
                for (EnviarEmailCommand.Adjunto adjunto : command.adjuntos()) {
                    helper.addAttachment(
                            adjunto.nombreArchivo(),
                            new ByteArrayResource(adjunto.contenido()),
                            adjunto.contentType()
                    );
                }
            });
            return EnviarEmailResult.ok();
        } catch (RuntimeException ex) {
            LOGGER.warn(
                    "Fallo enviando Email. notificacionId={}, correo={}, causa={}",
                    command.notificacionId(),
                    command.correo(),
                    ex.getMessage()
            );
            return EnviarEmailResult.error(ex.getMessage());
        }
    }
}
