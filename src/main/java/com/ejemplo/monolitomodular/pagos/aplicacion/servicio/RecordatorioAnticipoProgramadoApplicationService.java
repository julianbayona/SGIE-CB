package com.ejemplo.monolitomodular.pagos.aplicacion.servicio;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.NotificacionView;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.puerto.entrada.CrearNotificacionUseCase;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.ProgramarRecordatorioAnticipoCommand;
import com.ejemplo.monolitomodular.pagos.aplicacion.dto.RecordatorioAnticipoView;
import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.ProcesarRecordatoriosAnticipoProgramadosUseCase;
import com.ejemplo.monolitomodular.pagos.aplicacion.puerto.entrada.ProgramarRecordatorioAnticipoUseCase;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.RecordatorioAnticipo;
import com.ejemplo.monolitomodular.pagos.dominio.puerto.salida.AnticipoRepository;
import com.ejemplo.monolitomodular.pagos.dominio.puerto.salida.RecordatorioAnticipoRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class RecordatorioAnticipoProgramadoApplicationService implements ProgramarRecordatorioAnticipoUseCase, ProcesarRecordatoriosAnticipoProgramadosUseCase {

    private final RecordatorioAnticipoRepository recordatorioRepository;
    private final EventoRepository eventoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final CotizacionRepository cotizacionRepository;
    private final AnticipoRepository anticipoRepository;
    private final CrearNotificacionUseCase crearNotificacionUseCase;
    private final ObjectMapper objectMapper;

    public RecordatorioAnticipoProgramadoApplicationService(
            RecordatorioAnticipoRepository recordatorioRepository,
            EventoRepository eventoRepository,
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            CotizacionRepository cotizacionRepository,
            AnticipoRepository anticipoRepository,
            CrearNotificacionUseCase crearNotificacionUseCase,
            ObjectMapper objectMapper
    ) {
        this.recordatorioRepository = recordatorioRepository;
        this.eventoRepository = eventoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.anticipoRepository = anticipoRepository;
        this.crearNotificacionUseCase = crearNotificacionUseCase;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public RecordatorioAnticipoView ejecutar(ProgramarRecordatorioAnticipoCommand command) {
        usuarioRepository.buscarPorId(command.usuarioId())
                .orElseThrow(() -> new DomainException("Usuario no encontrado"));
        Evento evento = eventoRepository.buscarPorId(command.eventoId())
                .orElseThrow(() -> new DomainException("Evento no encontrado"));
        if (evento.getEstado() == EstadoEvento.CANCELADO) {
            throw new DomainException("No se puede programar recordatorio para un evento cancelado");
        }
        if (command.fechaRecordatorio().isAfter(evento.getFechaHoraInicio().toLocalDate())) {
            throw new DomainException("La fecha del recordatorio no puede ser posterior a la fecha del evento");
        }
        Cotizacion cotizacion = cotizacionAceptada(evento);
        validarSaldoPendiente(evento, cotizacion);
        if (recordatorioRepository.existePendientePorEventoYFecha(evento.getId(), command.fechaRecordatorio())) {
            throw new DomainException("Ya existe un recordatorio pendiente para este evento en esa fecha");
        }
        RecordatorioAnticipo guardado = recordatorioRepository.guardar(RecordatorioAnticipo.programar(
                evento.getId(),
                command.usuarioId(),
                command.fechaRecordatorio()
        ));
        return toView(guardado);
    }

    @Override
    @Transactional
    public int procesar(int limite) {
        return recordatorioRepository.buscarPendientesHasta(LocalDate.now(), limite).stream()
                .mapToInt(this::procesarRecordatorio)
                .sum();
    }

    private int procesarRecordatorio(RecordatorioAnticipo recordatorio) {
        Evento evento = eventoRepository.buscarPorId(recordatorio.getEventoId()).orElse(null);
        if (evento == null || evento.getEstado() == EstadoEvento.CANCELADO) {
            recordatorioRepository.guardar(recordatorio.omitir());
            return 0;
        }
        Cotizacion cotizacion = cotizacionRepository.buscarAceptadaVigentePorEventoId(evento.getId()).orElse(null);
        if (cotizacion == null || !tieneSaldoPendiente(evento, cotizacion)) {
            recordatorioRepository.guardar(recordatorio.omitir());
            return 0;
        }
        Cliente cliente = clienteRepository.buscarPorId(evento.getClienteId())
                .orElseThrow(() -> new DomainException("Cliente no encontrado para recordatorio de anticipo"));
        NotificacionView notificacion = crearNotificacionUseCase.ejecutar(new CrearNotificacionCommand(
                evento.getId(),
                TipoNotificacion.RECORDATORIO_ANTICIPO,
                LocalDateTime.now(),
                payload(evento, cliente, cotizacion),
                List.of(new CrearNotificacionCommand.Destinatario(null, cliente.getTelefono(), cliente.getCorreo()))
        ));
        recordatorioRepository.guardar(recordatorio.marcarNotificacionCreada(notificacion.id()));
        return 1;
    }

    private Cotizacion cotizacionAceptada(Evento evento) {
        return cotizacionRepository.buscarAceptadaVigentePorEventoId(evento.getId())
                .orElseThrow(() -> new DomainException("El evento no tiene una cotizacion aceptada vigente"));
    }

    private void validarSaldoPendiente(Evento evento, Cotizacion cotizacion) {
        if (!tieneSaldoPendiente(evento, cotizacion)) {
            throw new DomainException("El evento no tiene saldo pendiente de anticipo");
        }
    }

    private boolean tieneSaldoPendiente(Evento evento, Cotizacion cotizacion) {
        return saldoPendiente(evento, cotizacion).compareTo(BigDecimal.ZERO) > 0;
    }

    private BigDecimal saldoPendiente(Evento evento, Cotizacion cotizacion) {
        return cotizacion.getValorTotal().subtract(anticipoRepository.totalPorEventoId(evento.getId()));
    }

    private String payload(Evento evento, Cliente cliente, Cotizacion cotizacion) {
        BigDecimal totalPagado = anticipoRepository.totalPorEventoId(evento.getId());
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "tipo", "RECORDATORIO_ANTICIPO",
                    "cliente", cliente.getNombreCompleto(),
                    "fechaEvento", evento.getFechaHoraInicio().toString(),
                    "valorTotal", cotizacion.getValorTotal(),
                    "totalPagado", totalPagado,
                    "saldoPendiente", cotizacion.getValorTotal().subtract(totalPagado)
            ));
        } catch (Exception ex) {
            return "{}";
        }
    }

    private RecordatorioAnticipoView toView(RecordatorioAnticipo recordatorio) {
        return new RecordatorioAnticipoView(
                recordatorio.getId(),
                recordatorio.getEventoId(),
                recordatorio.getUsuarioId(),
                recordatorio.getFechaRecordatorio(),
                recordatorio.getEstado(),
                recordatorio.getNotificacionId()
        );
    }
}
