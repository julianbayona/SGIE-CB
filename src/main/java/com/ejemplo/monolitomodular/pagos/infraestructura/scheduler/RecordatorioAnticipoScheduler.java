package com.ejemplo.monolitomodular.pagos.infraestructura.scheduler;

import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.ProcesarRecordatoriosAnticipoUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "sgie.recordatorios-anticipo.scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RecordatorioAnticipoScheduler {

    private final ProcesarRecordatoriosAnticipoUseCase procesarRecordatoriosAnticipoUseCase;
    private final int diasAntes;
    private final int repetirCadaHoras;
    private final int limite;

    public RecordatorioAnticipoScheduler(
            ProcesarRecordatoriosAnticipoUseCase procesarRecordatoriosAnticipoUseCase,
            @Value("${sgie.recordatorios-anticipo.dias-antes:7}") int diasAntes,
            @Value("${sgie.recordatorios-anticipo.repetir-cada-horas:24}") int repetirCadaHoras,
            @Value("${sgie.recordatorios-anticipo.scheduler.limite:20}") int limite
    ) {
        this.procesarRecordatoriosAnticipoUseCase = procesarRecordatoriosAnticipoUseCase;
        this.diasAntes = diasAntes;
        this.repetirCadaHoras = repetirCadaHoras;
        this.limite = limite;
    }

    @Scheduled(fixedDelayString = "${sgie.recordatorios-anticipo.scheduler.fixed-delay-ms:3600000}")
    public void procesarPendientes() {
        procesarRecordatoriosAnticipoUseCase.procesar(diasAntes, repetirCadaHoras, limite);
    }
}
