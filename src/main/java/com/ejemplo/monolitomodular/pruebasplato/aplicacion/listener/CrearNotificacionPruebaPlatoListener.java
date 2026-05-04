package com.ejemplo.monolitomodular.pruebasplato.aplicacion.listener;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.evento.PruebaPlatoProgramadaEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CrearNotificacionPruebaPlatoListener {

    private final CrearNotificacionUseCase crearNotificacionUseCase;

    public CrearNotificacionPruebaPlatoListener(CrearNotificacionUseCase crearNotificacionUseCase) {
        this.crearNotificacionUseCase = crearNotificacionUseCase;
    }

    @EventListener
    public void manejar(PruebaPlatoProgramadaEvent event) {
        crearNotificacionUseCase.ejecutar(new CrearNotificacionCommand(
                event.eventoId(),
                TipoNotificacion.PRUEBA_PLATO_CLIENTE,
                LocalDateTime.now(),
                payload(event),
                List.of(new CrearNotificacionCommand.Destinatario(null, event.telefonoCliente()))
        ));
    }

    private String payload(PruebaPlatoProgramadaEvent event) {
        return """
                {"tipo":"PRUEBA_PLATO","cliente":"%s","fechaRealizacion":"%s"}
                """.formatted(event.nombreCliente(), event.fechaRealizacion()).trim();
    }
}
