package com.ejemplo.monolitomodular.notificaciones.aplicacion.servicio;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarWhatsAppCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.NotificacionView;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.ProcesarNotificacionesPendientesUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.Notificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.NotificacionRepository;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.WhatsAppPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificacionApplicationService implements CrearNotificacionUseCase, ProcesarNotificacionesPendientesUseCase {

    private final NotificacionRepository notificacionRepository;
    private final WhatsAppPort whatsAppPort;

    public NotificacionApplicationService(
            NotificacionRepository notificacionRepository,
            WhatsAppPort whatsAppPort
    ) {
        this.notificacionRepository = notificacionRepository;
        this.whatsAppPort = whatsAppPort;
    }

    @Override
    @Transactional
    public NotificacionView ejecutar(CrearNotificacionCommand command) {
        Notificacion notificacion = Notificacion.programar(
                command.eventoId(),
                command.tipoNotificacionId(),
                command.fechaProgramada(),
                command.payloadJson(),
                command.destinatarios().stream()
                        .map(destinatario -> new Notificacion.DestinatarioNuevo(
                                destinatario.usuarioId(),
                                destinatario.telefono()
                        ))
                        .toList()
        );
        return toView(notificacionRepository.guardar(notificacion));
    }

    @Override
    @Transactional
    public int procesarPendientes(int limite) {
        return notificacionRepository.buscarPendientes(LocalDateTime.now(), limite).stream()
                .mapToInt(this::procesar)
                .sum();
    }

    private int procesar(Notificacion notificacion) {
        Notificacion enEnvio = notificacionRepository.guardar(notificacion.iniciarEnvio());
        boolean exitoso = enEnvio.getDestinatarios().stream()
                .map(destinatario -> whatsAppPort.enviar(new EnviarWhatsAppCommand(
                        enEnvio.getId(),
                        destinatario.getTelefono(),
                        enEnvio.getPayloadJson()
                )))
                .allMatch(resultado -> resultado.exitoso());
        notificacionRepository.guardar(exitoso ? enEnvio.marcarEnviada() : enEnvio.marcarError());
        return exitoso ? 1 : 0;
    }

    private NotificacionView toView(Notificacion notificacion) {
        return new NotificacionView(
                notificacion.getId(),
                notificacion.getEventoId(),
                notificacion.getTipoNotificacionId(),
                notificacion.getFechaProgramada(),
                notificacion.getFechaEnvio(),
                notificacion.getEstado(),
                notificacion.getIntentos(),
                notificacion.getPayloadJson()
        );
    }
}
