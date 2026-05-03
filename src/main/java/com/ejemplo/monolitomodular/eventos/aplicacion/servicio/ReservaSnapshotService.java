package com.ejemplo.monolitomodular.eventos.aplicacion.servicio;

import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.HistorialEstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.HistorialEstadoEventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.menus.dominio.modelo.ItemMenu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Menu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.SeleccionMenu;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.MenuRepository;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.AdicionalEvento;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.InfraestructuraReserva;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.Montaje;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.MontajeMesaReserva;
import com.ejemplo.monolitomodular.montajes.dominio.puerto.salida.MontajeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReservaSnapshotService {

    private final ReservaSalonRepository reservaSalonRepository;
    private final MontajeRepository montajeRepository;
    private final MenuRepository menuRepository;
    private final CotizacionRepository cotizacionRepository;
    private final EventoRepository eventoRepository;
    private final HistorialEstadoEventoRepository historialEstadoEventoRepository;

    public ReservaSnapshotService(
            ReservaSalonRepository reservaSalonRepository,
            MontajeRepository montajeRepository,
            MenuRepository menuRepository,
            CotizacionRepository cotizacionRepository,
            EventoRepository eventoRepository,
            HistorialEstadoEventoRepository historialEstadoEventoRepository
    ) {
        this.reservaSalonRepository = reservaSalonRepository;
        this.montajeRepository = montajeRepository;
        this.menuRepository = menuRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.eventoRepository = eventoRepository;
        this.historialEstadoEventoRepository = historialEstadoEventoRepository;
    }

    public ReservaSalon crearNuevaVersionCopiandoComponentes(
            ReservaSalon reservaActual,
            UUID usuarioId,
            boolean copiarMontaje,
            boolean copiarMenu
    ) {
        ReservaSalon nuevaVersion = reservaActual.crearNuevaVersion(
                reservaActual.getSalonId(),
                reservaActual.getNumInvitados(),
                reservaActual.getFechaHoraInicio(),
                reservaActual.getFechaHoraFin(),
                usuarioId
        );
        reservaSalonRepository.desactivarReservaVigente(reservaActual.getReservaRaizId());
        cotizacionRepository.desactualizarActivasPorReservaId(reservaActual.getId());
        volverEventoAPendienteSiAplica(reservaActual, usuarioId);
        ReservaSalon guardada = reservaSalonRepository.guardar(nuevaVersion);

        if (copiarMontaje) {
            montajeRepository.buscarPorReservaId(reservaActual.getId())
                    .map(montaje -> copiarMontaje(montaje, guardada.getId()))
                    .ifPresent(montajeRepository::guardar);
        }
        if (copiarMenu) {
            menuRepository.buscarPorReservaId(reservaActual.getId())
                    .map(menu -> copiarMenu(menu, guardada.getId()))
                    .ifPresent(menuRepository::guardar);
        }

        return guardada;
    }

    private void volverEventoAPendienteSiAplica(ReservaSalon reservaActual, UUID usuarioId) {
        Evento evento = eventoRepository.buscarPorId(reservaActual.getEventoId())
                .orElseThrow(() -> new DomainException("Evento asociado a la reserva no encontrado"));
        Evento actualizado = evento.volverAPendientePorCotizacionDesactualizada();
        if (actualizado.getEstado() != evento.getEstado()) {
            eventoRepository.guardar(actualizado);
            historialEstadoEventoRepository.guardar(HistorialEstadoEvento.registrarCambio(
                    evento.getId(),
                    usuarioId,
                    evento.getEstado(),
                    actualizado.getEstado()
            ));
        }
    }

    private Montaje copiarMontaje(Montaje montajeAnterior, UUID nuevaReservaId) {
        UUID nuevoMontajeId = UUID.randomUUID();
        List<MontajeMesaReserva> mesas = montajeAnterior.getMesas().stream()
                .map(mesa -> MontajeMesaReserva.nueva(
                        nuevoMontajeId,
                        mesa.getTipoMesaId(),
                        mesa.getTipoSillaId(),
                        mesa.getSillaPorMesa(),
                        mesa.getCantidadMesas(),
                        mesa.getMantelId(),
                        mesa.getSobremantelId(),
                        mesa.isVajilla(),
                        mesa.isFajon()
                ))
                .toList();
        InfraestructuraReserva infraestructura = InfraestructuraReserva.nueva(
                nuevoMontajeId,
                montajeAnterior.getInfraestructura().isMesaPonque(),
                montajeAnterior.getInfraestructura().isMesaRegalos(),
                montajeAnterior.getInfraestructura().isEspacioMusicos(),
                montajeAnterior.getInfraestructura().isEstanteBombas()
        );
        List<AdicionalEvento> adicionales = montajeAnterior.getAdicionales().stream()
                .map(adicional -> AdicionalEvento.nuevo(
                        nuevoMontajeId,
                        adicional.getTipoAdicionalId(),
                        adicional.getCantidad()
                ))
                .toList();
        return Montaje.configurar(
                nuevoMontajeId,
                nuevaReservaId,
                montajeAnterior.getObservaciones(),
                mesas,
                infraestructura,
                adicionales
        );
    }

    private Menu copiarMenu(Menu menuAnterior, UUID nuevaReservaId) {
        UUID nuevoMenuId = UUID.randomUUID();
        List<SeleccionMenu> selecciones = menuAnterior.getSelecciones().stream()
                .map(seleccion -> copiarSeleccion(seleccion, nuevoMenuId))
                .toList();
        return Menu.configurar(nuevoMenuId, nuevaReservaId, menuAnterior.getNotasGenerales(), selecciones);
    }

    private SeleccionMenu copiarSeleccion(SeleccionMenu seleccionAnterior, UUID nuevoMenuId) {
        UUID nuevaSeleccionId = UUID.randomUUID();
        List<ItemMenu> items = seleccionAnterior.getItems().stream()
                .map(item -> ItemMenu.nuevo(
                        nuevaSeleccionId,
                        item.getPlatoId(),
                        item.getCantidad(),
                        item.getExcepciones()
                ))
                .toList();
        return SeleccionMenu.nueva(nuevaSeleccionId, nuevoMenuId, seleccionAnterior.getTipoMomentoId(), items);
    }
}
