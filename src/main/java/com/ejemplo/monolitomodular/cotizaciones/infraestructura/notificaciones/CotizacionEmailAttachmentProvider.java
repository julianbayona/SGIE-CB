package com.ejemplo.monolitomodular.cotizaciones.infraestructura.notificaciones;

import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.DocumentoCotizacionView;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.DescargarDocumentoCotizacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.salida.EmailAttachmentProvider;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CotizacionEmailAttachmentProvider implements EmailAttachmentProvider {

    private final ObjectProvider<DescargarDocumentoCotizacionUseCase> descargarDocumentoCotizacionUseCase;
    private final ObjectMapper objectMapper;

    public CotizacionEmailAttachmentProvider(
            ObjectProvider<DescargarDocumentoCotizacionUseCase> descargarDocumentoCotizacionUseCase,
            ObjectMapper objectMapper
    ) {
        this.descargarDocumentoCotizacionUseCase = descargarDocumentoCotizacionUseCase;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<EnviarEmailCommand.Adjunto> adjuntos(TipoNotificacion tipo, String payloadJson) {
        if (tipo != TipoNotificacion.COTIZACION_CLIENTE) {
            return List.of();
        }
        UUID cotizacionId = cotizacionId(payloadJson);
        DocumentoCotizacionView documento = descargarDocumentoCotizacionUseCase.getObject().descargar(cotizacionId);
        return List.of(new EnviarEmailCommand.Adjunto(
                documento.nombreArchivo(),
                documento.contentType(),
                documento.contenido()
        ));
    }

    private UUID cotizacionId(String payloadJson) {
        try {
            JsonNode payload = objectMapper.readTree(payloadJson);
            JsonNode valor = payload.get("cotizacionId");
            if (valor == null || valor.asText().isBlank()) {
                throw new IllegalArgumentException("El payload no contiene cotizacionId");
            }
            return UUID.fromString(valor.asText());
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("No fue posible leer cotizacionId del payload", ex);
        }
    }
}
