package com.ejemplo.monolitomodular.pagos.infraestructura.scheduler;

import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.ProcesarRecordatoriosAnticipoProgramadosUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "sgie.recordatorios-anticipo.programados.scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RecordatorioAnticipoProgramadoScheduler {

    private final ProcesarRecordatoriosAnticipoProgramadosUseCase procesarUseCase;
    private final int limite;

    public RecordatorioAnticipoProgramadoScheduler(
            ProcesarRecordatoriosAnticipoProgramadosUseCase procesarUseCase,
            @Value("${sgie.recordatorios-anticipo.programados.scheduler.limite:50}") int limite
    ) {
        this.procesarUseCase = procesarUseCase;
        this.limite = limite;
    }

    @Scheduled(
            cron = "${sgie.recordatorios-anticipo.programados.scheduler.cron:0 0 9 * * *}",
            zone = "${sgie.recordatorios-anticipo.programados.scheduler.zone:America/Bogota}"
    )
    public void procesarPendientes() {
        procesarUseCase.procesar(limite);
    }
}
