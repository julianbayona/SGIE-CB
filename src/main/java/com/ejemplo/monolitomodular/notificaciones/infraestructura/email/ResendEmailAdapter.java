package com.ejemplo.monolitomodular.notificaciones.infraestructura.email;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailResult;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.EmailPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Base64;
import java.util.List;

@Component
@ConditionalOnExpression("'${sgie.notificaciones.email.enabled:false}' == 'true' && '${sgie.notificaciones.email.provider:smtp}' == 'resend'")
public class ResendEmailAdapter implements EmailPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendEmailAdapter.class);

    private final RestClient restClient;
    private final String apiKey;
    private final String from;

    public ResendEmailAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${sgie.notificaciones.email.resend.api-url}") String apiUrl,
            @Value("${sgie.notificaciones.email.resend.api-key:}") String apiKey,
            @Value("${sgie.notificaciones.email.from:}") String from
    ) {
        this.restClient = restClientBuilder.baseUrl(apiUrl).build();
        this.apiKey = apiKey;
        this.from = from;
    }

    @Override
    public EnviarEmailResult enviar(EnviarEmailCommand command) {
        if (from == null || from.isBlank()) {
            return EnviarEmailResult.error("No esta configurado sgie.notificaciones.email.from");
        }
        if (apiKey == null || apiKey.isBlank()) {
            return EnviarEmailResult.error("No esta configurado RESEND_API_KEY");
        }

        ResendEmailRequest request = new ResendEmailRequest(
                from.trim(),
                List.of(command.correo()),
                command.asunto(),
                command.cuerpo(),
                command.adjuntos().stream().map(this::toAttachment).toList()
        );

        try {
            ResendEmailResponse response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .body(request)
                    .retrieve()
                    .body(ResendEmailResponse.class);

            LOGGER.info(
                    "Email enviado via Resend. notificacionId={}, correo={}, resendId={}",
                    command.notificacionId(),
                    command.correo(),
                    response == null ? null : response.id()
            );
            return EnviarEmailResult.ok();
        } catch (RestClientResponseException ex) {
            String mensaje = "Resend rechazo el envio con estado " + ex.getStatusCode().value() + ": " + ex.getResponseBodyAsString();
            LOGGER.warn(
                    "Fallo enviando Email via Resend. notificacionId={}, correo={}, causa={}",
                    command.notificacionId(),
                    command.correo(),
                    mensaje
            );
            return EnviarEmailResult.error(mensaje);
        } catch (RestClientException ex) {
            LOGGER.warn(
                    "Fallo enviando Email via Resend. notificacionId={}, correo={}, causa={}",
                    command.notificacionId(),
                    command.correo(),
                    ex.getMessage()
            );
            return EnviarEmailResult.error(ex.getMessage());
        }
    }

    private ResendAttachment toAttachment(EnviarEmailCommand.Adjunto adjunto) {
        return new ResendAttachment(
                adjunto.nombreArchivo(),
                Base64.getEncoder().encodeToString(adjunto.contenido()),
                adjunto.contentType()
        );
    }

    private record ResendEmailRequest(
            String from,
            List<String> to,
            String subject,
            String text,
            List<ResendAttachment> attachments
    ) {
    }

    private record ResendAttachment(
            String filename,
            String content,
            String content_type
    ) {
    }

    private record ResendEmailResponse(String id) {
    }
}
