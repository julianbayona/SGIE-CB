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
    private final String correoChef;
    private final String correoGerente;
    private final String correoTesorero;

    public CrearNotificacionPersonalPruebaPlatoListener(
            CrearNotificacionUseCase crearNotificacionUseCase,
            @Value("${sgie.notificaciones.prueba-plato.chef-telefono:}") String telefonoChef,
            @Value("${sgie.notificaciones.prueba-plato.gerente-telefono:}") String telefonoGerente,
            @Value("${sgie.notificaciones.prueba-plato.tesorero-telefono:}") String telefonoTesorero,
            @Value("${sgie.notificaciones.prueba-plato.chef-correo:}") String correoChef,
            @Value("${sgie.notificaciones.prueba-plato.gerente-correo:}") String correoGerente,
            @Value("${sgie.notificaciones.prueba-plato.tesorero-correo:}") String correoTesorero
    ) {
        this.crearNotificacionUseCase = crearNotificacionUseCase;
        this.telefonoChef = telefonoChef;
        this.telefonoGerente = telefonoGerente;
        this.telefonoTesorero = telefonoTesorero;
        this.correoChef = correoChef;
        this.correoGerente = correoGerente;
        this.correoTesorero = correoTesorero;
    }

    @EventListener
    public void manejar(PruebaPlatoProgramadaEvent event) {
        List<CrearNotificacionCommand.Destinatario> destinatarios = List.of(
                destinatario(telefonoChef, correoChef),
                destinatario(telefonoGerente, correoGerente),
                destinatario(telefonoTesorero, correoTesorero)
        ).stream()
                .filter(destinatario -> destinatario.telefono() != null || destinatario.correo() != null)
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

    private CrearNotificacionCommand.Destinatario destinatario(String telefono, String correo) {
        return new CrearNotificacionCommand.Destinatario(null, normalizar(telefono), normalizar(correo));
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
