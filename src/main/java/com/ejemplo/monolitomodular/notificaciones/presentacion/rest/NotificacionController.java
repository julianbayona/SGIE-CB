package com.ejemplo.monolitomodular.notificaciones.presentacion.rest;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.NotificacionDestinatarioView;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.NotificacionDetalleView;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.ListarNotificacionesPorEventoUseCase;
import com.ejemplo.monolitomodular.notificaciones.presentacion.rest.dto.NotificacionDestinatarioResponse;
import com.ejemplo.monolitomodular.notificaciones.presentacion.rest.dto.NotificacionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/eventos/{eventoId}/notificaciones")
public class NotificacionController {

    private final ListarNotificacionesPorEventoUseCase listarNotificacionesPorEventoUseCase;

    public NotificacionController(ListarNotificacionesPorEventoUseCase listarNotificacionesPorEventoUseCase) {
        this.listarNotificacionesPorEventoUseCase = listarNotificacionesPorEventoUseCase;
    }

    @GetMapping
    public List<NotificacionResponse> listarPorEvento(@PathVariable UUID eventoId) {
        return listarNotificacionesPorEventoUseCase.listarPorEvento(eventoId).stream()
                .map(this::toResponse)
                .toList();
    }

    private NotificacionResponse toResponse(NotificacionDetalleView notificacion) {
        return new NotificacionResponse(
                notificacion.id(),
                notificacion.eventoId(),
                notificacion.tipo(),
                notificacion.fechaProgramada(),
                notificacion.fechaEnvio(),
                notificacion.estado(),
                notificacion.intentos(),
                notificacion.payloadJson(),
                notificacion.destinatarios().stream().map(this::toResponse).toList()
        );
    }

    private NotificacionDestinatarioResponse toResponse(NotificacionDestinatarioView destinatario) {
        return new NotificacionDestinatarioResponse(
                destinatario.id(),
                destinatario.usuarioId(),
                destinatario.telefono(),
                destinatario.correo(),
                destinatario.estado()
        );
    }
}
