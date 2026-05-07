package com.ejemplo.monolitomodular.eventos.aplicacion.servicio;

import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoComidaRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoEventoRepository;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.EventoCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.modelo.TipoOperacionCalendar;
import com.ejemplo.monolitomodular.calendario.dominio.puerto.salida.EventoCalendarRepository;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CancelarEventoCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.evento.EventoConfirmadoEvent;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CrearEventoCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CrearReservaSalonCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.ModificarReservaSalonCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.ReservaSalonView;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.CancelarEventoUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.ConfirmarEventoUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.ConsultarEventoUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.CrearEventoUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.CrearReservaSalonUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.ModificarReservaSalonUseCase;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.HistorialEstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.HistorialEstadoEventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.Notificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.NotificacionRepository;
import com.ejemplo.monolitomodular.pagos.dominio.modelo.RecordatorioAnticipo;
import com.ejemplo.monolitomodular.pagos.dominio.puerto.salida.RecordatorioAnticipoRepository;
import com.ejemplo.monolitomodular.pruebasplato.dominio.modelo.PruebaPlato;
import com.ejemplo.monolitomodular.pruebasplato.dominio.puerto.salida.PruebaPlatoRepository;
import com.ejemplo.monolitomodular.salones.dominio.puerto.salida.SalonRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class EventoApplicationService implements
        CrearEventoUseCase,
        ConsultarEventoUseCase,
        CrearReservaSalonUseCase,
        ModificarReservaSalonUseCase,
        ConfirmarEventoUseCase,
        CancelarEventoUseCase {

    private final ClienteRepository clienteRepository;
    private final TipoEventoRepository tipoEventoRepository;
    private final TipoComidaRepository tipoComidaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SalonRepository salonRepository;
    private final EventoRepository eventoRepository;
    private final ReservaSalonRepository reservaSalonRepository;
    private final HistorialEstadoEventoRepository historialEstadoEventoRepository;
    private final CotizacionRepository cotizacionRepository;
    private final PruebaPlatoRepository pruebaPlatoRepository;
    private final RecordatorioAnticipoRepository recordatorioAnticipoRepository;
    private final NotificacionRepository notificacionRepository;
    private final EventoCalendarRepository eventoCalendarRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public EventoApplicationService(
            ClienteRepository clienteRepository,
            TipoEventoRepository tipoEventoRepository,
            TipoComidaRepository tipoComidaRepository,
            UsuarioRepository usuarioRepository,
            SalonRepository salonRepository,
            EventoRepository eventoRepository,
            ReservaSalonRepository reservaSalonRepository,
            HistorialEstadoEventoRepository historialEstadoEventoRepository,
            CotizacionRepository cotizacionRepository,
            PruebaPlatoRepository pruebaPlatoRepository,
            RecordatorioAnticipoRepository recordatorioAnticipoRepository,
            NotificacionRepository notificacionRepository,
            EventoCalendarRepository eventoCalendarRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.clienteRepository = clienteRepository;
        this.tipoEventoRepository = tipoEventoRepository;
        this.tipoComidaRepository = tipoComidaRepository;
        this.usuarioRepository = usuarioRepository;
        this.salonRepository = salonRepository;
        this.eventoRepository = eventoRepository;
        this.reservaSalonRepository = reservaSalonRepository;
        this.historialEstadoEventoRepository = historialEstadoEventoRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.pruebaPlatoRepository = pruebaPlatoRepository;
        this.recordatorioAnticipoRepository = recordatorioAnticipoRepository;
        this.notificacionRepository = notificacionRepository;
        this.eventoCalendarRepository = eventoCalendarRepository;
        this.eventPublisher = eventPublisher;
    }

    public EventoApplicationService(
            ClienteRepository clienteRepository,
            TipoEventoRepository tipoEventoRepository,
            TipoComidaRepository tipoComidaRepository,
            UsuarioRepository usuarioRepository,
            SalonRepository salonRepository,
            EventoRepository eventoRepository,
            ReservaSalonRepository reservaSalonRepository,
            HistorialEstadoEventoRepository historialEstadoEventoRepository,
            CotizacionRepository cotizacionRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this(
                clienteRepository,
                tipoEventoRepository,
                tipoComidaRepository,
                usuarioRepository,
                salonRepository,
                eventoRepository,
                reservaSalonRepository,
                historialEstadoEventoRepository,
                cotizacionRepository,
                new NoOpPruebaPlatoRepository(),
                new NoOpRecordatorioAnticipoRepository(),
                new NoOpNotificacionRepository(),
                new NoOpEventoCalendarRepository(),
                eventPublisher
        );
    }

    @Override
    @Transactional
    public EventoView ejecutar(CrearEventoCommand command) {
        clienteRepository.buscarPorId(command.clienteId())
                .orElseThrow(() -> new DomainException("Cliente no encontrado"));

        if (!tipoEventoRepository.existeActivoPorId(command.tipoEventoId())) {
            throw new DomainException("El tipo de evento no existe o esta inactivo");
        }

        if (!tipoComidaRepository.existeActivoPorId(command.tipoComidaId())) {
            throw new DomainException("El tipo de comida no existe o esta inactivo");
        }

        usuarioRepository.buscarPorId(command.usuarioCreadorId())
                .orElseThrow(() -> new DomainException("Usuario creador no encontrado"));

        validarRango(command);

        Evento evento = Evento.nuevo(
                command.clienteId(),
                command.tipoEventoId(),
                command.tipoComidaId(),
                command.usuarioCreadorId(),
                command.fechaHoraInicio(),
                command.fechaHoraFin()
        );
        Evento guardado = eventoRepository.guardar(evento);
        historialEstadoEventoRepository.guardar(
                HistorialEstadoEvento.registrarCreacion(guardado.getId(), command.usuarioCreadorId())
        );

        return toView(guardado, List.of());
    }

    @Override
    @Transactional
    public EventoView ejecutar(UUID eventoId, CrearReservaSalonCommand command) {
        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new DomainException("Evento no encontrado"));

        usuarioRepository.buscarPorId(command.usuarioId())
                .orElseThrow(() -> new DomainException("Usuario no encontrado"));

        if (salonRepository.buscarTodosPorIds(Set.of(command.salonId())).isEmpty()) {
            throw new DomainException("El salon no existe");
        }

        validarRango(command.fechaHoraInicio(), command.fechaHoraFin());

        if (reservaSalonRepository.existeConflicto(command.salonId(), command.fechaHoraInicio(), command.fechaHoraFin())) {
            throw new DomainException("Ya existe una reserva confirmada en conflicto para el salon " + command.salonId());
        }

        reservaSalonRepository.guardar(ReservaSalon.nueva(
                eventoId,
                command.salonId(),
                command.numInvitados(),
                command.fechaHoraInicio(),
                command.fechaHoraFin(),
                command.usuarioId()
        ));

        return toView(evento, reservaSalonRepository.listarPorEvento(eventoId));
    }

    @Override
    @Transactional
    public EventoView ejecutar(UUID reservaRaizId, ModificarReservaSalonCommand command) {
        ReservaSalon reservaActual = reservaSalonRepository.buscarVigentePorRaizId(reservaRaizId)
                .orElseThrow(() -> new DomainException("No existe una reserva vigente para el identificador indicado"));

        Evento evento = eventoRepository.buscarPorId(reservaActual.getEventoId())
                .orElseThrow(() -> new DomainException("Evento no encontrado"));

        usuarioRepository.buscarPorId(command.usuarioId())
                .orElseThrow(() -> new DomainException("Usuario no encontrado"));

        if (salonRepository.buscarTodosPorIds(Set.of(command.salonId())).isEmpty()) {
            throw new DomainException("El salon destino no existe");
        }

        validarRango(command.fechaHoraInicio(), command.fechaHoraFin());

        if (reservaSalonRepository.existeConflicto(
                command.salonId(),
                command.fechaHoraInicio(),
                command.fechaHoraFin(),
                reservaActual.getReservaRaizId()
        )) {
            throw new DomainException("Ya existe una reserva confirmada en conflicto para el salon " + command.salonId());
        }

        reservaSalonRepository.desactivarReservaVigente(reservaActual.getReservaRaizId());
        reservaSalonRepository.guardar(
                reservaActual.crearNuevaVersion(
                        command.salonId(),
                        command.numInvitados(),
                        command.fechaHoraInicio(),
                        command.fechaHoraFin(),
                        command.usuarioId()
                )
        );

        return toView(evento, reservaSalonRepository.listarPorEvento(evento.getId()));
    }

    @Override
    public EventoView obtenerPorId(UUID id) {
        Evento evento = eventoRepository.buscarPorId(id)
                .orElseThrow(() -> new DomainException("Evento no encontrado"));
        return toView(evento, reservaSalonRepository.listarPorEvento(id));
    }

    @Override
    public List<EventoView> listar() {
        return eventoRepository.listar().stream()
                .map(evento -> toView(evento, reservaSalonRepository.listarPorEvento(evento.getId())))
                .toList();
    }

    @Override
    @Transactional
    public EventoView confirmar(UUID eventoId, UUID usuarioId) {
        usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new DomainException("Usuario no encontrado"));
        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new DomainException("Evento no encontrado"));
        validarEventoConfirmable(evento);

        Evento confirmado = evento.confirmar();
        if (confirmado.getEstado() != evento.getEstado()) {
            eventoRepository.guardar(confirmado);
            historialEstadoEventoRepository.guardar(HistorialEstadoEvento.registrarCambio(
                    evento.getId(),
                    usuarioId,
                    evento.getEstado(),
                    confirmado.getEstado()
            ));
            eventPublisher.publishEvent(new EventoConfirmadoEvent(
                    confirmado.getId(),
                    confirmado.getClienteId(),
                    confirmado.getFechaHoraInicio(),
                    confirmado.getFechaHoraFin()
            ));
        }
        return toView(confirmado, reservaSalonRepository.listarPorEvento(eventoId));
    }

    @Override
    @Transactional
    public EventoView cancelar(CancelarEventoCommand command) {
        Usuario usuario = usuarioRepository.buscarPorId(command.usuarioId())
                .orElseThrow(() -> new DomainException("Usuario no encontrado"));
        if (usuario.getRol() != RolUsuario.ADMINISTRADOR) {
            throw new DomainException("Solo un administrador puede cancelar eventos");
        }
        String motivo = validarMotivoCancelacion(command.motivo());

        Evento evento = eventoRepository.buscarPorId(command.eventoId())
                .orElseThrow(() -> new DomainException("Evento no encontrado"));
        Evento cancelado = evento.cancelar();

        eventoRepository.guardar(cancelado);
        historialEstadoEventoRepository.guardar(HistorialEstadoEvento.registrarCambioConMotivo(
                evento.getId(),
                usuario.getId(),
                evento.getEstado(),
                cancelado.getEstado(),
                motivo
        ));

        cotizacionRepository.desactualizarActivasPorEventoId(evento.getId());
        cancelarPruebasPlato(evento.getId());
        cancelarRecordatoriosAnticipo(evento.getId());
        cancelarSincronizacionesCalendar(evento.getId());

        return toView(cancelado, reservaSalonRepository.listarPorEvento(evento.getId()));
    }

    private void validarEventoConfirmable(Evento evento) {
        if (cotizacionRepository.buscarAceptadaVigentePorEventoId(evento.getId()).isEmpty()) {
            throw new DomainException("El evento debe tener una cotizacion aceptada vigente para confirmarse");
        }
        if (reservaSalonRepository.listarPorEvento(evento.getId()).isEmpty()) {
            throw new DomainException("El evento debe tener al menos una reserva de salon vigente para confirmarse");
        }
        if (reservaSalonRepository.existeConflictoParaEvento(evento.getId())) {
            throw new DomainException("No se puede confirmar el evento porque existe conflicto con una reserva confirmada");
        }
    }

    private void validarRango(CrearEventoCommand command) {
        validarRango(command.fechaHoraInicio(), command.fechaHoraFin());
    }

    private void validarRango(java.time.LocalDateTime fechaHoraInicio, java.time.LocalDateTime fechaHoraFin) {
        if (fechaHoraInicio == null) {
            throw new DomainException("La fecha y hora de inicio del evento es obligatoria");
        }
        if (fechaHoraFin == null) {
            throw new DomainException("La fecha y hora de fin del evento es obligatoria");
        }
        if (!fechaHoraFin.isAfter(fechaHoraInicio)) {
            throw new DomainException("La fecha y hora de fin debe ser posterior a la fecha y hora de inicio");
        }
    }

    private String validarMotivoCancelacion(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new DomainException("El motivo de cancelacion es obligatorio");
        }
        String valor = motivo.trim();
        if (valor.length() > 500) {
            throw new DomainException("El motivo de cancelacion no puede superar 500 caracteres");
        }
        return valor;
    }

    private void cancelarPruebasPlato(UUID eventoId) {
        pruebaPlatoRepository.buscarProgramadasPorEventoId(eventoId).stream()
                .map(prueba -> prueba.cancelar())
                .forEach(pruebaPlatoRepository::guardar);
        cancelarNotificacionesPendientes(eventoId, TipoNotificacion.PRUEBA_PLATO_CLIENTE);
        cancelarNotificacionesPendientes(eventoId, TipoNotificacion.PRUEBA_PLATO_PERSONAL);
    }

    private void cancelarRecordatoriosAnticipo(UUID eventoId) {
        recordatorioAnticipoRepository.buscarCancelablesPorEventoId(eventoId).stream()
                .map(recordatorio -> recordatorio.cancelar())
                .forEach(recordatorioAnticipoRepository::guardar);
        cancelarNotificacionesPendientes(eventoId, TipoNotificacion.RECORDATORIO_ANTICIPO);
    }

    private void cancelarNotificacionesPendientes(UUID eventoId, TipoNotificacion tipo) {
        notificacionRepository.buscarCancelablesPorEventoYTipo(eventoId, tipo).stream()
                .map(Notificacion::cancelar)
                .forEach(notificacionRepository::guardar);
    }

    private void cancelarSincronizacionesCalendar(UUID eventoId) {
        List<EventoCalendar> sincronizados = eventoCalendarRepository.buscarSincronizadosCancelablesPorEventoId(eventoId);
        eventoCalendarRepository.cancelarPendientesPorEventoId(eventoId);

        Set<String> googleEventIdsCancelados = new HashSet<>();
        sincronizados.stream()
                .filter(eventoCalendar -> eventoCalendar.getGoogleEventId() != null)
                .filter(eventoCalendar -> googleEventIdsCancelados.add(eventoCalendar.getGoogleEventId()))
                .map(eventoCalendar -> EventoCalendar.pendienteConGoogleEventId(
                        eventoCalendar.getOrigenTipo(),
                        eventoCalendar.getOrigenId(),
                        eventoCalendar.getEventoId(),
                        TipoOperacionCalendar.CANCELAR,
                        eventoCalendar.getGoogleEventId(),
                        "{}"
                ))
                .forEach(eventoCalendarRepository::guardar);
    }

    private EventoView toView(Evento evento, List<ReservaSalon> reservas) {
        return new EventoView(
                evento.getId(),
                evento.getClienteId(),
                evento.getTipoEventoId(),
                evento.getTipoComidaId(),
                evento.getUsuarioCreadorId(),
                evento.getEstado(),
                evento.getGcalEventId(),
                evento.getFechaHoraInicio(),
                evento.getFechaHoraFin(),
                reservas.stream().map(this::toReservaView).toList()
        );
    }

    private ReservaSalonView toReservaView(ReservaSalon reserva) {
        return new ReservaSalonView(
                reserva.getId(),
                reserva.getReservaRaizId(),
                reserva.getSalonId(),
                reserva.getNumInvitados(),
                reserva.getFechaHoraInicio(),
                reserva.getFechaHoraFin(),
                reserva.getVersion(),
                reserva.isVigente()
        );
    }

    private static class NoOpPruebaPlatoRepository implements PruebaPlatoRepository {

        @Override
        public PruebaPlato guardar(PruebaPlato pruebaPlato) {
            return pruebaPlato;
        }
    }

    private static class NoOpRecordatorioAnticipoRepository implements RecordatorioAnticipoRepository {

        @Override
        public RecordatorioAnticipo guardar(RecordatorioAnticipo recordatorio) {
            return recordatorio;
        }

        @Override
        public java.util.Optional<RecordatorioAnticipo> buscarPorId(UUID id) {
            return java.util.Optional.empty();
        }

        @Override
        public boolean existePendientePorEventoYFecha(UUID eventoId, java.time.LocalDate fechaRecordatorio) {
            return false;
        }

        @Override
        public List<RecordatorioAnticipo> buscarPendientesHasta(java.time.LocalDate fechaReferencia, int limite) {
            return List.of();
        }
    }

    private static class NoOpNotificacionRepository implements NotificacionRepository {

        @Override
        public Notificacion guardar(Notificacion notificacion) {
            return notificacion;
        }

        @Override
        public List<Notificacion> buscarPendientes(java.time.LocalDateTime fechaReferencia, int limite) {
            return List.of();
        }

        @Override
        public boolean existePorEventoYTipoDesde(UUID eventoId, TipoNotificacion tipo, java.time.LocalDateTime fechaDesde) {
            return false;
        }
    }

    private static class NoOpEventoCalendarRepository implements EventoCalendarRepository {

        @Override
        public EventoCalendar guardar(EventoCalendar eventoCalendar) {
            return eventoCalendar;
        }

        @Override
        public java.util.Optional<EventoCalendar> buscarPorId(UUID id) {
            return java.util.Optional.empty();
        }

        @Override
        public List<EventoCalendar> buscarPendientes(int limite) {
            return List.of();
        }
    }
}
