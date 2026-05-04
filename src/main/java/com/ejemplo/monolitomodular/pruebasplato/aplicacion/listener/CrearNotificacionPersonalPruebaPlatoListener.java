package com.ejemplo.monolitomodular.pruebasplato.aplicacion.listener;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.evento.PruebaPlatoProgramadaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CrearNotificacionPersonalPruebaPlatoListener {

    private final CrearNotificacionUseCase crearNotificacionUseCase;
    private final String telefonoChef;
    private final String telefonoGerente;
    private final String telefonoTesorero;

    public CrearNotificacionPersonalPruebaPlatoListener(
            CrearNotificacionUseCase crearNotificacionUseCase,
            @Value("${sgie.notificaciones.prueba-plato.chef-telefono:}") String telefonoChef,
            @Value("${sgie.notificaciones.prueba-plato.gerente-telefono:}") String telefonoGerente,
            @Value("${sgie.notificaciones.prueba-plato.tesorero-telefono:}") String telefonoTesorero
    ) {
        this.crearNotificacionUseCase = crearNotificacionUseCase;
        this.telefonoChef = telefonoChef;
        this.telefonoGerente = telefonoGerente;
        this.telefonoTesorero = telefonoTesorero;
    }

    @EventListener
    public void manejar(PruebaPlatoProgramadaEvent event) {
        List<CrearNotificacionCommand.Destinatario> destinatarios = List.of(telefonoChef, telefonoGerente, telefonoTesorero)
                .stream()
                .map(String::trim)
                .filter(telefono -> !telefono.isBlank())
                .map(telefono -> new CrearNotificacionCommand.Destinatario(null, telefono))
                .toList();
        if (destinatarios.size() != 3) {
            return;
        }
        crearNotificacionUseCase.ejecutar(new CrearNotificacionCommand(
                event.eventoId(),
                TipoNotificacion.PRUEBA_PLATO_PERSONAL,
                LocalDateTime.now(),
                payload(event),
                destinatarios
        ));
    }

    private String payload(PruebaPlatoProgramadaEvent event) {
        return """
                {"tipo":"PRUEBA_PLATO_PERSONAL","cliente":"%s","fechaRealizacion":"%s"}
                """.formatted(event.nombreCliente(), event.fechaRealizacion()).trim();
    }
}
