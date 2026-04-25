package com.ejemplo.monolitomodular.eventos.aplicacion.servicio;

import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CrearEventoCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.ConsultarEventoUseCase;
import com.ejemplo.monolitomodular.eventos.aplicacion.puerto.entrada.CrearEventoUseCase;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.HistorialEstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.HistorialEstadoEventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.salones.dominio.puerto.salida.SalonRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class EventoApplicationService implements CrearEventoUseCase, ConsultarEventoUseCase {

    private final ClienteRepository clienteRepository;
    private final SalonRepository salonRepository;
    private final EventoRepository eventoRepository;
    private final ReservaSalonRepository reservaSalonRepository;
    private final HistorialEstadoEventoRepository historialEstadoEventoRepository;
    private final UsuarioRepository usuarioRepository;

    public EventoApplicationService(
            ClienteRepository clienteRepository,
            SalonRepository salonRepository,
            EventoRepository eventoRepository,
            ReservaSalonRepository reservaSalonRepository,
            HistorialEstadoEventoRepository historialEstadoEventoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.salonRepository = salonRepository;
        this.eventoRepository = eventoRepository;
        this.reservaSalonRepository = reservaSalonRepository;
        this.historialEstadoEventoRepository = historialEstadoEventoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public EventoView ejecutar(CrearEventoCommand command) {
        clienteRepository.buscarPorId(command.clienteId())
                .orElseThrow(() -> new DomainException("Cliente no encontrado"));

        if (command.usuarioResponsableId() != null) {
            usuarioRepository.buscarPorId(command.usuarioResponsableId())
                    .orElseThrow(() -> new DomainException("Usuario responsable no encontrado"));
        }

        Set<UUID> salonIds = normalizarSalones(command.salonIds());
        if (salonRepository.buscarTodosPorIds(salonIds).size() != salonIds.size()) {
            throw new DomainException("Uno o mas salones no existen");
        }

        Evento evento = Evento.nuevo(
                command.clienteId(),
                command.tipoEvento(),
                command.tipoComida(),
                command.fechaEvento(),
                command.horaInicio(),
                command.duracionHoras(),
                command.numeroPersonas(),
                command.observaciones()
        );

        LocalDateTime fechaInicio = LocalDateTime.of(evento.getFechaEvento(), evento.getHoraInicio());
        LocalDateTime fechaFin = LocalDateTime.of(evento.getFechaEvento(), evento.getHoraFin());

        for (UUID salonId : salonIds) {
            if (reservaSalonRepository.existeConflicto(salonId, fechaInicio, fechaFin)) {
                throw new DomainException("Ya existe una reserva en conflicto para el salon " + salonId);
            }
        }

        Evento guardado = eventoRepository.guardar(evento);

        List<ReservaSalon> reservas = salonIds.stream()
                .map(salonId -> ReservaSalon.nueva(guardado.getId(), salonId, fechaInicio, fechaFin))
                .toList();
        reservaSalonRepository.guardarTodas(reservas);
        historialEstadoEventoRepository.guardar(
                HistorialEstadoEvento.registrarCreacion(guardado.getId(), command.usuarioResponsableId())
        );

        return toView(guardado, reservas);
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

    private Set<UUID> normalizarSalones(List<UUID> salonIds) {
        if (salonIds == null || salonIds.isEmpty()) {
            throw new DomainException("Debe enviar al menos un salon para el evento");
        }
        return new LinkedHashSet<>(salonIds);
    }

    private EventoView toView(Evento evento, List<ReservaSalon> reservas) {
        return new EventoView(
                evento.getId(),
                evento.getClienteId(),
                evento.getTipoEvento(),
                evento.getTipoComida(),
                evento.getFechaEvento(),
                evento.getHoraInicio(),
                evento.getHoraFin(),
                evento.getNumeroPersonas(),
                evento.getEstado(),
                evento.getObservaciones(),
                reservas.stream().map(ReservaSalon::getSalonId).toList()
        );
    }
}
