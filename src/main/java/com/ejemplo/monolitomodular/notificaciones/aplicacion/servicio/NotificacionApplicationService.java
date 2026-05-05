package com.ejemplo.monolitomodular.notificaciones.aplicacion.servicio;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarEmailCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarWhatsAppCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.NotificacionView;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.ProcesarNotificacionesPendientesUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.Notificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoDestinatarioNotificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.NotificacionDestinatario;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.EmailPort;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.NotificacionRepository;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.WhatsAppPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificacionApplicationService implements CrearNotificacionUseCase, ProcesarNotificacionesPendientesUseCase {

    private final NotificacionRepository notificacionRepository;
    private final WhatsAppPort whatsAppPort;
    private final EmailPort emailPort;
    private final NotificacionEmailFormatter emailFormatter = new NotificacionEmailFormatter();

    public NotificacionApplicationService(
            NotificacionRepository notificacionRepository,
            WhatsAppPort whatsAppPort,
            EmailPort emailPort
    ) {
        this.notificacionRepository = notificacionRepository;
        this.whatsAppPort = whatsAppPort;
        this.emailPort = emailPort;
    }

    @Override
    @Transactional
    public NotificacionView ejecutar(CrearNotificacionCommand command) {
        Notificacion notificacion = Notificacion.programar(
                command.eventoId(),
                command.tipo(),
                command.fechaProgramada(),
                command.payloadJson(),
                command.destinatarios().stream()
                        .map(destinatario -> new Notificacion.DestinatarioNuevo(
                                destinatario.usuarioId(),
                                destinatario.telefono(),
                                destinatario.correo()
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
        List<NotificacionDestinatario> destinatariosActualizados = new ArrayList<>();
        for (NotificacionDestinatario destinatario : enEnvio.getDestinatarios()) {
            destinatariosActualizados.add(procesarDestinatario(enEnvio, destinatario));
        }
        Notificacion procesada = notificacionRepository.guardar(enEnvio.finalizarProcesamiento(destinatariosActualizados));
        return procesada.getDestinatarios().stream()
                .allMatch(destinatario -> destinatario.getEstado() == EstadoDestinatarioNotificacion.ENVIADO) ? 1 : 0;
    }

    private NotificacionDestinatario procesarDestinatario(Notificacion notificacion, NotificacionDestinatario destinatario) {
        if (destinatario.getEstado() == EstadoDestinatarioNotificacion.ENVIADO) {
            return destinatario;
        }
        boolean exitoso = enviarWhatsAppSiAplica(notificacion, destinatario)
                && enviarEmailSiAplica(notificacion, destinatario);
        return exitoso ? destinatario.marcarEnviado() : destinatario.marcarError();
    }

    private boolean enviarWhatsAppSiAplica(Notificacion notificacion, NotificacionDestinatario destinatario) {
        if (!destinatario.tieneTelefono()) {
            return true;
        }
        return whatsAppPort.enviar(new EnviarWhatsAppCommand(
                notificacion.getId(),
                destinatario.getTelefono(),
                notificacion.getPayloadJson()
        )).exitoso();
    }

    private boolean enviarEmailSiAplica(Notificacion notificacion, NotificacionDestinatario destinatario) {
        if (!destinatario.tieneCorreo()) {
            return true;
        }
        NotificacionEmailFormatter.EmailMessage emailMessage = emailFormatter.formatear(
                notificacion.getTipo(),
                notificacion.getPayloadJson()
        );
        return emailPort.enviar(new EnviarEmailCommand(
                notificacion.getId(),
                destinatario.getCorreo(),
                notificacion.getTipo(),
                emailMessage.asunto(),
                emailMessage.cuerpo()
        )).exitoso();
    }

    private NotificacionView toView(Notificacion notificacion) {
        return new NotificacionView(
                notificacion.getId(),
                notificacion.getEventoId(),
                notificacion.getTipo(),
                notificacion.getFechaProgramada(),
                notificacion.getFechaEnvio(),
                notificacion.getEstado(),
                notificacion.getIntentos(),
                notificacion.getPayloadJson()
        );
    }
}
