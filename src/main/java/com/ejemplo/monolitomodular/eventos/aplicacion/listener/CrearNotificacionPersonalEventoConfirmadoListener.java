package com.ejemplo.monolitomodular.eventos.aplicacion.listener;

import com.ejemplo.monolitomodular.eventos.aplicacion.evento.EventoConfirmadoEvent;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CrearNotificacionPersonalEventoConfirmadoListener {

    private final CrearNotificacionUseCase crearNotificacionUseCase;
    private final String grupoWhatsAppPersonalId;

    public CrearNotificacionPersonalEventoConfirmadoListener(
            CrearNotificacionUseCase crearNotificacionUseCase,
            @Value("${sgie.notificaciones.personal.grupo-whatsapp-id:}") String grupoWhatsAppPersonalId
    ) {
        this.crearNotificacionUseCase = crearNotificacionUseCase;
        this.grupoWhatsAppPersonalId = grupoWhatsAppPersonalId;
    }

    @EventListener
    public void manejar(EventoConfirmadoEvent event) {
        if (grupoWhatsAppPersonalId == null || grupoWhatsAppPersonalId.isBlank()) {
            return;
        }
        crearNotificacionUseCase.ejecutar(new CrearNotificacionCommand(
                event.eventoId(),
                TipoNotificacion.EVENTO_CONFIRMADO_PERSONAL,
                LocalDateTime.now(),
                payload(event),
                List.of(new CrearNotificacionCommand.Destinatario(null, grupoWhatsAppPersonalId.trim()))
        ));
    }

    private String payload(EventoConfirmadoEvent event) {
        return """
                {"tipo":"EVENTO_CONFIRMADO_PERSONAL","fechaInicio":"%s","fechaFin":"%s"}
                """.formatted(event.fechaHoraInicio(), event.fechaHoraFin()).trim();
    }
}
