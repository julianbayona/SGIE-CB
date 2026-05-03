package com.ejemplo.monolitomodular.cotizaciones.aplicacion.servicio;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.ModoCobroAdicional;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoAdicional;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoAdicionalRepository;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.ActualizarItemCotizacionCommand;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.CotizacionItemView;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.CotizacionView;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.GenerarCotizacionCommand;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.ActualizarItemCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.ConsultarCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.EnviarCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.GenerarDocumentoCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.puerto.entrada.GenerarCotizacionUseCase;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.CotizacionItem;
import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.HistorialEstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.HistorialEstadoEventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.menus.dominio.modelo.ItemMenu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Menu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Plato;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.MenuRepository;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.PlatoRepository;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.AdicionalEvento;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.Montaje;
import com.ejemplo.monolitomodular.montajes.dominio.puerto.salida.MontajeRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CotizacionApplicationService implements
        GenerarCotizacionUseCase,
        ConsultarCotizacionUseCase,
        ActualizarItemCotizacionUseCase,
        GenerarDocumentoCotizacionUseCase,
        EnviarCotizacionUseCase {

    private final ReservaSalonRepository reservaSalonRepository;
    private final UsuarioRepository usuarioRepository;
    private final CotizacionRepository cotizacionRepository;
    private final MenuRepository menuRepository;
    private final MontajeRepository montajeRepository;
    private final PlatoRepository platoRepository;
    private final TipoAdicionalRepository tipoAdicionalRepository;
    private final EventoRepository eventoRepository;
    private final HistorialEstadoEventoRepository historialEstadoEventoRepository;

    public CotizacionApplicationService(
            ReservaSalonRepository reservaSalonRepository,
            UsuarioRepository usuarioRepository,
            CotizacionRepository cotizacionRepository,
            MenuRepository menuRepository,
            MontajeRepository montajeRepository,
            PlatoRepository platoRepository,
            TipoAdicionalRepository tipoAdicionalRepository,
            EventoRepository eventoRepository,
            HistorialEstadoEventoRepository historialEstadoEventoRepository
    ) {
        this.reservaSalonRepository = reservaSalonRepository;
        this.usuarioRepository = usuarioRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.menuRepository = menuRepository;
        this.montajeRepository = montajeRepository;
        this.platoRepository = platoRepository;
        this.tipoAdicionalRepository = tipoAdicionalRepository;
        this.eventoRepository = eventoRepository;
        this.historialEstadoEventoRepository = historialEstadoEventoRepository;
    }

    @Override
    @Transactional
    public CotizacionView ejecutar(GenerarCotizacionCommand command) {
        ReservaSalon reserva = reservaSalonRepository.buscarVigentePorRaizId(command.reservaRaizId())
                .orElseThrow(() -> new DomainException("No existe una reserva vigente para el identificador indicado"));
        usuarioRepository.buscarPorId(command.usuarioId())
                .orElseThrow(() -> new DomainException("Usuario no encontrado"));
        if (cotizacionRepository.buscarActivaPorReservaId(reserva.getId()).isPresent()) {
            throw new DomainException("Ya existe una cotizacion activa para la reserva vigente");
        }

        UUID cotizacionId = UUID.randomUUID();
        List<CotizacionItem> items = construirItemsDesdeReserva(cotizacionId, reserva);
        Cotizacion cotizacion = Cotizacion.crearBorrador(
                cotizacionId,
                reserva.getId(),
                command.usuarioId(),
                command.descuento(),
                command.observaciones(),
                items
        );
        return toView(cotizacionRepository.guardar(cotizacion));
    }

    @Override
    public CotizacionView obtenerPorId(UUID id) {
        return cotizacionRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Cotizacion no encontrada"));
    }

    @Override
    @Transactional
    public CotizacionView ejecutar(ActualizarItemCotizacionCommand command) {
        Cotizacion cotizacion = cotizacionRepository.buscarPorId(command.cotizacionId())
                .orElseThrow(() -> new DomainException("Cotizacion no encontrada"));
        return toView(cotizacionRepository.guardar(cotizacion.actualizarItem(command.itemId(), command.precioOverride())));
    }

    @Override
    @Transactional
    public CotizacionView generar(UUID cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.buscarPorId(cotizacionId)
                .orElseThrow(() -> new DomainException("Cotizacion no encontrada"));
        return toView(cotizacionRepository.guardar(cotizacion.generarDocumento()));
    }

    @Override
    @Transactional
    public CotizacionView enviar(UUID cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.buscarPorId(cotizacionId)
                .orElseThrow(() -> new DomainException("Cotizacion no encontrada"));
        Cotizacion enviada = cotizacionRepository.guardar(cotizacion.enviar());
        actualizarEvento(enviada, Evento::marcarCotizacionEnviada);
        return toView(enviada);
    }

    @Override
    @Transactional
    public CotizacionView aceptar(UUID cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.buscarPorId(cotizacionId)
                .orElseThrow(() -> new DomainException("Cotizacion no encontrada"));
        Cotizacion aceptada = cotizacionRepository.guardar(cotizacion.aceptar());
        actualizarEvento(aceptada, Evento::marcarCotizacionAprobada);
        return toView(aceptada);
    }

    @Override
    @Transactional
    public CotizacionView rechazar(UUID cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.buscarPorId(cotizacionId)
                .orElseThrow(() -> new DomainException("Cotizacion no encontrada"));
        return toView(cotizacionRepository.guardar(cotizacion.rechazar()));
    }

    private void actualizarEvento(Cotizacion cotizacion, java.util.function.Function<Evento, Evento> transicion) {
        ReservaSalon reserva = reservaSalonRepository.buscarPorId(cotizacion.getReservaId())
                .orElseThrow(() -> new DomainException("Reserva asociada a la cotizacion no encontrada"));
        Evento evento = eventoRepository.buscarPorId(reserva.getEventoId())
                .orElseThrow(() -> new DomainException("Evento asociado a la cotizacion no encontrado"));
        Evento actualizado = transicion.apply(evento);
        if (actualizado.getEstado() != evento.getEstado()) {
            eventoRepository.guardar(actualizado);
            historialEstadoEventoRepository.guardar(HistorialEstadoEvento.registrarCambio(
                    evento.getId(),
                    cotizacion.getUsuarioId(),
                    evento.getEstado(),
                    actualizado.getEstado()
            ));
        }
    }

    private List<CotizacionItem> construirItemsDesdeReserva(UUID cotizacionId, ReservaSalon reserva) {
        Menu menu = menuRepository.buscarPorReservaId(reserva.getId())
                .orElseThrow(() -> new DomainException("No existe menu configurado para la reserva"));
        Montaje montaje = montajeRepository.buscarPorReservaId(reserva.getId())
                .orElseThrow(() -> new DomainException("No existe montaje configurado para la reserva"));
        Map<ItemOrigen, BigDecimal> preciosNegociados = preciosNegociadosAnteriores(reserva.getReservaRaizId());

        List<CotizacionItem> items = new ArrayList<>();
        menu.getSelecciones().forEach(seleccion -> seleccion.getItems()
                .forEach(itemMenu -> items.add(construirItemMenu(cotizacionId, itemMenu, preciosNegociados))));
        montaje.getAdicionales()
                .forEach(adicional -> items.add(construirItemAdicional(cotizacionId, adicional, preciosNegociados)));
        return items;
    }

    private Map<ItemOrigen, BigDecimal> preciosNegociadosAnteriores(UUID reservaRaizId) {
        return cotizacionRepository.buscarUltimaPorReservaRaizId(reservaRaizId)
                .stream()
                .flatMap(cotizacion -> cotizacion.getItems().stream())
                .filter(item -> item.getPrecioOverride() != null)
                .collect(Collectors.toMap(
                        item -> new ItemOrigen(item.getTipoConcepto(), item.getOrigenId()),
                        CotizacionItem::getPrecioOverride,
                        (actual, repetido) -> actual
                ));
    }

    private CotizacionItem construirItemMenu(UUID cotizacionId, ItemMenu itemMenu, Map<ItemOrigen, BigDecimal> preciosNegociados) {
        Plato plato = platoRepository.buscarPorId(itemMenu.getPlatoId())
                .orElseThrow(() -> new DomainException("Plato no encontrado para generar la cotizacion"));
        BigDecimal precioBase = plato.getPrecioBase();
        String tipoConcepto = "MENU";
        return CotizacionItem.nuevo(
                cotizacionId,
                tipoConcepto,
                itemMenu.getPlatoId(),
                plato.getNombre(),
                precioBase,
                preciosNegociados.get(new ItemOrigen(tipoConcepto, itemMenu.getPlatoId())),
                itemMenu.getCantidad()
        );
    }

    private CotizacionItem construirItemAdicional(UUID cotizacionId, AdicionalEvento adicional, Map<ItemOrigen, BigDecimal> preciosNegociados) {
        TipoAdicional tipoAdicional = tipoAdicionalRepository.buscarPorId(adicional.getTipoAdicionalId())
                .orElseThrow(() -> new DomainException("Tipo adicional no encontrado para generar la cotizacion"));
        int cantidadCotizada = tipoAdicional.getModoCobro() == ModoCobroAdicional.SERVICIO ? 1 : adicional.getCantidad();
        String tipoConcepto = "ADICIONAL";
        return CotizacionItem.nuevo(
                cotizacionId,
                tipoConcepto,
                adicional.getTipoAdicionalId(),
                tipoAdicional.getNombre(),
                tipoAdicional.getPrecioBase(),
                preciosNegociados.get(new ItemOrigen(tipoConcepto, adicional.getTipoAdicionalId())),
                cantidadCotizada
        );
    }

    private CotizacionView toView(Cotizacion cotizacion) {
        return new CotizacionView(
                cotizacion.getId(),
                cotizacion.getReservaId(),
                cotizacion.getUsuarioId(),
                cotizacion.getEstado(),
                cotizacion.getValorSubtotal(),
                cotizacion.getDescuento(),
                cotizacion.getValorTotal(),
                cotizacion.getObservaciones(),
                cotizacion.getItems().stream().map(this::toView).toList()
        );
    }

    private CotizacionItemView toView(CotizacionItem item) {
        return new CotizacionItemView(
                item.getId(),
                item.getTipoConcepto(),
                item.getOrigenId(),
                item.getDescripcion(),
                item.getPrecioBase(),
                item.getPrecioOverride(),
                item.getCantidad(),
                item.getSubtotal()
        );
    }

    private record ItemOrigen(String tipoConcepto, UUID origenId) {
    }
}
