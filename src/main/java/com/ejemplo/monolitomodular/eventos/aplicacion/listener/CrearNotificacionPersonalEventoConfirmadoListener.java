package com.ejemplo.monolitomodular.eventos.aplicacion.listener;

import com.ejemplo.monolitomodular.eventos.aplicacion.evento.EventoConfirmadoEvent;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class CrearNotificacionPersonalEventoConfirmadoListener {

    private final CrearNotificacionUseCase crearNotificacionUseCase;
    private final String grupoWhatsAppPersonalId;
    private final String correosPersonal;

    public CrearNotificacionPersonalEventoConfirmadoListener(
            CrearNotificacionUseCase crearNotificacionUseCase,
            @Value("${sgie.notificaciones.personal.grupo-whatsapp-id:}") String grupoWhatsAppPersonalId,
            @Value("${sgie.notificaciones.personal.correos:}") String correosPersonal
    ) {
        this.crearNotificacionUseCase = crearNotificacionUseCase;
        this.grupoWhatsAppPersonalId = grupoWhatsAppPersonalId;
        this.correosPersonal = correosPersonal;
    }

    @EventListener
    public void manejar(EventoConfirmadoEvent event) {
        List<CrearNotificacionCommand.Destinatario> destinatarios = destinatarios();
        if (destinatarios.isEmpty()) {
            return;
        }
        crearNotificacionUseCase.ejecutar(new CrearNotificacionCommand(
                event.eventoId(),
                TipoNotificacion.EVENTO_CONFIRMADO_PERSONAL,
                LocalDateTime.now(),
                payload(event),
                destinatarios
        ));
    }

    private List<CrearNotificacionCommand.Destinatario> destinatarios() {
        List<CrearNotificacionCommand.Destinatario> destinatarios = new java.util.ArrayList<>();
        if (grupoWhatsAppPersonalId != null && !grupoWhatsAppPersonalId.isBlank()) {
            destinatarios.add(new CrearNotificacionCommand.Destinatario(null, grupoWhatsAppPersonalId.trim(), null));
        }
        Arrays.stream((correosPersonal == null ? "" : correosPersonal).split(","))
                .map(String::trim)
                .filter(correo -> !correo.isBlank())
                .map(correo -> new CrearNotificacionCommand.Destinatario(null, null, correo))
                .forEach(destinatarios::add);
        return destinatarios;
    }

    private String payload(EventoConfirmadoEvent event) {
        return """
                {"tipo":"EVENTO_CONFIRMADO_PERSONAL","fechaInicio":"%s","fechaFin":"%s"}
                """.formatted(event.fechaHoraInicio(), event.fechaHoraFin()).trim();
    }
}
