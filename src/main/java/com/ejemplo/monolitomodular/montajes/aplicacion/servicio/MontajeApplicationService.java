package com.ejemplo.monolitomodular.montajes.aplicacion.servicio;

import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.MantelRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.SobremantelRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoMesaRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoSillaRepository;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.ConfigurarMontajeCommand;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.InfraestructuraReservaCommand;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.InfraestructuraReservaView;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.MontajeMesaReservaCommand;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.MontajeMesaReservaView;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.MontajeView;
import com.ejemplo.monolitomodular.montajes.aplicacion.puerto.entrada.ConfigurarMontajeUseCase;
import com.ejemplo.monolitomodular.montajes.aplicacion.puerto.entrada.ConsultarMontajeUseCase;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.InfraestructuraReserva;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.Montaje;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.MontajeMesaReserva;
import com.ejemplo.monolitomodular.montajes.dominio.puerto.salida.MontajeRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MontajeApplicationService implements ConfigurarMontajeUseCase, ConsultarMontajeUseCase {

    private final ReservaSalonRepository reservaSalonRepository;
    private final TipoMesaRepository tipoMesaRepository;
    private final TipoSillaRepository tipoSillaRepository;
    private final MantelRepository mantelRepository;
    private final SobremantelRepository sobremantelRepository;
    private final MontajeRepository montajeRepository;
    private final UsuarioRepository usuarioRepository;

    public MontajeApplicationService(
            ReservaSalonRepository reservaSalonRepository,
            TipoMesaRepository tipoMesaRepository,
            TipoSillaRepository tipoSillaRepository,
            MantelRepository mantelRepository,
            SobremantelRepository sobremantelRepository,
            MontajeRepository montajeRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.reservaSalonRepository = reservaSalonRepository;
        this.tipoMesaRepository = tipoMesaRepository;
        this.tipoSillaRepository = tipoSillaRepository;
        this.mantelRepository = mantelRepository;
        this.sobremantelRepository = sobremantelRepository;
        this.montajeRepository = montajeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public MontajeView ejecutar(ConfigurarMontajeCommand command) {
        ReservaSalon reserva = obtenerReservaVigente(command.reservaRaizId());
        usuarioRepository.buscarPorId(command.usuarioId())
                .orElseThrow(() -> new DomainException("Usuario no encontrado"));
        ReservaSalon reservaDestino = obtenerReservaDestino(reserva, command.usuarioId());
        UUID montajeId = UUID.randomUUID();

        if (command.mesas() == null || command.mesas().isEmpty()) {
            throw new DomainException("El montaje debe tener al menos una configuracion de mesas");
        }

        List<MontajeMesaReserva> mesas = command.mesas().stream()
                .peek(this::validarCatalogosMesa)
                .map(mesa -> toDomain(montajeId, mesa))
                .toList();

        InfraestructuraReserva infraestructura = toDomain(montajeId, command.infraestructura());
        Montaje montaje = Montaje.configurar(montajeId, reservaDestino.getId(), command.observaciones(), mesas, infraestructura);
        return toView(montajeRepository.guardar(montaje));
    }

    @Override
    public MontajeView obtenerPorReservaRaizId(UUID reservaRaizId) {
        ReservaSalon reserva = obtenerReservaVigente(reservaRaizId);
        return montajeRepository.buscarPorReservaId(reserva.getId())
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Montaje no encontrado para la reserva indicada"));
    }

    private ReservaSalon obtenerReservaVigente(UUID reservaRaizId) {
        return reservaSalonRepository.buscarVigentePorRaizId(reservaRaizId)
                .orElseThrow(() -> new DomainException("No existe una reserva vigente para el identificador indicado"));
    }

    private ReservaSalon obtenerReservaDestino(ReservaSalon reservaActual, UUID usuarioId) {
        if (montajeRepository.buscarPorReservaId(reservaActual.getId()).isEmpty()) {
            return reservaActual;
        }
        ReservaSalon nuevaVersion = reservaActual.crearNuevaVersion(
                reservaActual.getSalonId(),
                reservaActual.getNumInvitados(),
                reservaActual.getFechaHoraInicio(),
                reservaActual.getFechaHoraFin(),
                usuarioId
        );
        reservaSalonRepository.desactivarReservaVigente(reservaActual.getReservaRaizId());
        return reservaSalonRepository.guardar(nuevaVersion);
    }

    private void validarCatalogosMesa(MontajeMesaReservaCommand command) {
        if (!tipoMesaRepository.existeActivoPorId(command.tipoMesaId())) {
            throw new DomainException("El tipo de mesa no existe o esta inactivo");
        }
        if (!tipoSillaRepository.existeActivoPorId(command.tipoSillaId())) {
            throw new DomainException("El tipo de silla no existe o esta inactivo");
        }
        if (!mantelRepository.existeActivoPorId(command.mantelId())) {
            throw new DomainException("El mantel no existe o esta inactivo");
        }
        if (command.sobremantelId() != null && !sobremantelRepository.existeActivoPorId(command.sobremantelId())) {
            throw new DomainException("El sobremantel no existe o esta inactivo");
        }
    }

    private MontajeMesaReserva toDomain(UUID montajeId, MontajeMesaReservaCommand command) {
        return MontajeMesaReserva.nueva(
                montajeId,
                command.tipoMesaId(),
                command.tipoSillaId(),
                command.sillaPorMesa(),
                command.cantidadMesas(),
                command.mantelId(),
                command.sobremantelId(),
                command.vajilla(),
                command.fajon()
        );
    }

    private InfraestructuraReserva toDomain(UUID montajeId, InfraestructuraReservaCommand command) {
        if (command == null) {
            throw new DomainException("La infraestructura del montaje es obligatoria");
        }
        return InfraestructuraReserva.nueva(
                montajeId,
                command.mesaPonque(),
                command.mesaRegalos(),
                command.espacioMusicos(),
                command.estanteBombas()
        );
    }

    private MontajeView toView(Montaje montaje) {
        return new MontajeView(
                montaje.getId(),
                montaje.getReservaId(),
                montaje.getObservaciones(),
                montaje.getMesas().stream().map(this::toView).toList(),
                toView(montaje.getInfraestructura())
        );
    }

    private MontajeMesaReservaView toView(MontajeMesaReserva mesa) {
        return new MontajeMesaReservaView(
                mesa.getId(),
                mesa.getTipoMesaId(),
                mesa.getTipoSillaId(),
                mesa.getSillaPorMesa(),
                mesa.getCantidadMesas(),
                mesa.getMantelId(),
                mesa.getSobremantelId(),
                mesa.isVajilla(),
                mesa.isFajon()
        );
    }

    private InfraestructuraReservaView toView(InfraestructuraReserva infraestructura) {
        return new InfraestructuraReservaView(
                infraestructura.getId(),
                infraestructura.isMesaPonque(),
                infraestructura.isMesaRegalos(),
                infraestructura.isEspacioMusicos(),
                infraestructura.isEstanteBombas()
        );
    }
}
