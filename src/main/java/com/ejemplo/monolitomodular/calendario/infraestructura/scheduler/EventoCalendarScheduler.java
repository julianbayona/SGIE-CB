package com.ejemplo.monolitomodular.calendario.infraestructura.scheduler;

import com.ejemplo.monolitomodular.calendario.aplicacion.puerto.entrada.ProcesarEventosCalendarPendientesUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventoCalendarScheduler {

    private final ProcesarEventosCalendarPendientesUseCase procesarEventosCalendarPendientesUseCase;
    private final int limite;

    public EventoCalendarScheduler(
            ProcesarEventosCalendarPendientesUseCase procesarEventosCalendarPendientesUseCase,
            @Value("${sgie.calendario.scheduler.limite:20}") int limite
    ) {
        this.procesarEventosCalendarPendientesUseCase = procesarEventosCalendarPendientesUseCase;
        this.limite = limite;
    }

    @Scheduled(fixedDelayString = "${sgie.calendario.scheduler.fixed-delay-ms:60000}")
    public void procesarPendientes() {
        procesarEventosCalendarPendientesUseCase.procesarPendientes(limite);
    }
}
