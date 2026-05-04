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
    private final String telefonosPersonal;

    public CrearNotificacionPersonalEventoConfirmadoListener(
            CrearNotificacionUseCase crearNotificacionUseCase,
            @Value("${sgie.notificaciones.personal.telefonos:}") String telefonosPersonal
    ) {
        this.crearNotificacionUseCase = crearNotificacionUseCase;
        this.telefonosPersonal = telefonosPersonal;
    }

    @EventListener
    public void manejar(EventoConfirmadoEvent event) {
        List<CrearNotificacionCommand.Destinatario> destinatarios = Arrays.stream(telefonosPersonal.split(","))
                .map(String::trim)
                .filter(telefono -> !telefono.isBlank())
                .map(telefono -> new CrearNotificacionCommand.Destinatario(null, telefono))
                .toList();
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

    private String payload(EventoConfirmadoEvent event) {
        return """
                {"tipo":"EVENTO_CONFIRMADO_PERSONAL","fechaInicio":"%s","fechaFin":"%s"}
                """.formatted(event.fechaHoraInicio(), event.fechaHoraFin()).trim();
    }
}
