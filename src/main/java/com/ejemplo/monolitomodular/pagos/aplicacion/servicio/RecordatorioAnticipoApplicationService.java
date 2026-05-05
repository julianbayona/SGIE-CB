package com.ejemplo.monolitomodular.pagos.aplicacion.servicio;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.NotificacionRepository;
import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.ProcesarRecordatoriosAnticipoUseCase;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.EventoAnticipoPendiente;
import com.ejemplo.monolitomodular.pagos.dominio.puerto.salida.AnticipoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class RecordatorioAnticipoApplicationService implements ProcesarRecordatoriosAnticipoUseCase {

    private final AnticipoRepository anticipoRepository;
    private final NotificacionRepository notificacionRepository;
    private final CrearNotificacionUseCase crearNotificacionUseCase;
    private final ObjectMapper objectMapper;

    public RecordatorioAnticipoApplicationService(
            AnticipoRepository anticipoRepository,
            NotificacionRepository notificacionRepository,
            CrearNotificacionUseCase crearNotificacionUseCase,
            ObjectMapper objectMapper
    ) {
        this.anticipoRepository = anticipoRepository;
        this.notificacionRepository = notificacionRepository;
        this.crearNotificacionUseCase = crearNotificacionUseCase;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public int procesar(int diasAntes, int repetirCadaHoras, int limite) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaLimite = ahora.plusDays(diasAntes);
        LocalDateTime fechaRepeticion = ahora.minusHours(repetirCadaHoras);
        return anticipoRepository.buscarEventosConAnticipoPendiente(ahora, fechaLimite, limite).stream()
                .filter(candidato -> !notificacionRepository.existePorEventoYTipoDesde(
                        candidato.eventoId(),
                        TipoNotificacion.RECORDATORIO_ANTICIPO,
                        fechaRepeticion
                ))
                .mapToInt(this::crearRecordatorio)
                .sum();
    }

    private int crearRecordatorio(EventoAnticipoPendiente candidato) {
        crearNotificacionUseCase.ejecutar(new CrearNotificacionCommand(
                candidato.eventoId(),
                TipoNotificacion.RECORDATORIO_ANTICIPO,
                LocalDateTime.now(),
                payload(candidato),
                List.of(new CrearNotificacionCommand.Destinatario(
                        null,
                        candidato.telefonoCliente(),
                        candidato.correoCliente()
                ))
        ));
        return 1;
    }

    private String payload(EventoAnticipoPendiente candidato) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "tipo", "RECORDATORIO_ANTICIPO",
                    "cliente", candidato.nombreCliente(),
                    "fechaEvento", candidato.fechaHoraInicio().toString(),
                    "valorTotal", candidato.valorTotal(),
                    "totalPagado", candidato.totalPagado(),
                    "saldoPendiente", candidato.saldoPendiente()
            ));
        } catch (Exception ex) {
            return "{}";
        }
    }
}
