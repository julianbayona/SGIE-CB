package com.ejemplo.monolitomodular.notificaciones.infraestructura.scheduler;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.ProcesarNotificacionesPendientesUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificacionScheduler {

    private final ProcesarNotificacionesPendientesUseCase procesarNotificacionesPendientesUseCase;
    private final int limite;

    public NotificacionScheduler(
            ProcesarNotificacionesPendientesUseCase procesarNotificacionesPendientesUseCase,
            @Value("${sgie.notificaciones.scheduler.limite:20}") int limite
    ) {
        this.procesarNotificacionesPendientesUseCase = procesarNotificacionesPendientesUseCase;
        this.limite = limite;
    }

    @Scheduled(fixedDelayString = "${sgie.notificaciones.scheduler.fixed-delay-ms:60000}")
    public void procesarPendientes() {
        procesarNotificacionesPendientesUseCase.procesarPendientes(limite);
    }
}
