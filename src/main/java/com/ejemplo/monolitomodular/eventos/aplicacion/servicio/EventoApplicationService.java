package com.ejemplo.monolitomodular.eventos.aplicacion.servicio;

import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoComidaRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoEventoRepository;
import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.evento.EventoConfirmadoEvent;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CrearEventoCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CrearReservaSalonCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.ModificarReservaSalonCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.ReservaSalonView;
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
import com.ejemplo.monolitomodular.salones.dominio.puerto.salida.SalonRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class EventoApplicationService implements
        CrearEventoUseCase,
        ConsultarEventoUseCase,
        CrearReservaSalonUseCase,
        ModificarReservaSalonUseCase,
        ConfirmarEventoUseCase {

    private final ClienteRepository clienteRepository;
    private final TipoEventoRepository tipoEventoRepository;
    private final TipoComidaRepository tipoComidaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SalonRepository salonRepository;
    private final EventoRepository eventoRepository;
    private final ReservaSalonRepository reservaSalonRepository;
    private final HistorialEstadoEventoRepository historialEstadoEventoRepository;
    private final CotizacionRepository cotizacionRepository;
    private final ApplicationEventPublisher eventPublisher;

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
        this.clienteRepository = clienteRepository;
        this.tipoEventoRepository = tipoEventoRepository;
        this.tipoComidaRepository = tipoComidaRepository;
        this.usuarioRepository = usuarioRepository;
        this.salonRepository = salonRepository;
        this.eventoRepository = eventoRepository;
        this.reservaSalonRepository = reservaSalonRepository;
        this.historialEstadoEventoRepository = historialEstadoEventoRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.eventPublisher = eventPublisher;
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
}
