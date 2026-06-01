package com.ejemplo.monolitomodular.notificaciones.infraestructura.email;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailResult;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ResendEmailAdapterTest {

    @Test
    void enviaEmailConAdjuntosCodificadosEnBase64() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        ResendEmailAdapter adapter = new ResendEmailAdapter(
                builder,
                "https://api.resend.com/emails",
                "re_test",
                "cotizaciones@club.test"
        );

        server.expect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer re_test"))
                .andExpect(jsonPath("$.from").value("cotizaciones@club.test"))
                .andExpect(jsonPath("$.to[0]").value("cliente@example.com"))
                .andExpect(jsonPath("$.subject").value("Cotizacion"))
                .andExpect(jsonPath("$.attachments[0].filename").value("cotizacion.pdf"))
                .andExpect(jsonPath("$.attachments[0].content").value("UERG"))
                .andExpect(jsonPath("$.attachments[0].content_type").value("application/pdf"))
                .andRespond(withSuccess("{\"id\":\"email_123\"}", MediaType.APPLICATION_JSON));

        EnviarEmailResult result = adapter.enviar(new EnviarEmailCommand(
                UUID.randomUUID(),
                "cliente@example.com",
                TipoNotificacion.COTIZACION_CLIENTE,
                "Cotizacion",
                "Adjuntamos la cotizacion.",
                List.of(new EnviarEmailCommand.Adjunto(
                        "cotizacion.pdf",
                        "application/pdf",
                        "PDF".getBytes(StandardCharsets.UTF_8)
                ))
        ));

        assertTrue(result.exitoso());
        server.verify();
    }
}
