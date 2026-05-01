package com.ejemplo.monolitomodular.menus.aplicacion.servicio;

import com.ejemplo.monolitomodular.eventos.aplicacion.servicio.ReservaSnapshotService;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.ConfigurarMenuCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.ItemMenuCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.ItemMenuView;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.MenuView;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.SeleccionMenuCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.SeleccionMenuView;
import com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada.ConfigurarMenuUseCase;
import com.ejemplo.monolitomodular.menus.aplicacion.puerto.entrada.ConsultarMenuUseCase;
import com.ejemplo.monolitomodular.menus.dominio.modelo.ItemMenu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Menu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.SeleccionMenu;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.MenuRepository;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.PlatoRepository;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.TipoMomentoMenuRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MenuApplicationService implements ConfigurarMenuUseCase, ConsultarMenuUseCase {

    private final ReservaSalonRepository reservaSalonRepository;
    private final UsuarioRepository usuarioRepository;
    private final MenuRepository menuRepository;
    private final TipoMomentoMenuRepository tipoMomentoMenuRepository;
    private final PlatoRepository platoRepository;
    private final ReservaSnapshotService reservaSnapshotService;

    public MenuApplicationService(
            ReservaSalonRepository reservaSalonRepository,
            UsuarioRepository usuarioRepository,
            MenuRepository menuRepository,
            TipoMomentoMenuRepository tipoMomentoMenuRepository,
            PlatoRepository platoRepository,
            ReservaSnapshotService reservaSnapshotService
    ) {
        this.reservaSalonRepository = reservaSalonRepository;
        this.usuarioRepository = usuarioRepository;
        this.menuRepository = menuRepository;
        this.tipoMomentoMenuRepository = tipoMomentoMenuRepository;
        this.platoRepository = platoRepository;
        this.reservaSnapshotService = reservaSnapshotService;
    }

    @Override
    @Transactional
    public MenuView ejecutar(ConfigurarMenuCommand command) {
        ReservaSalon reserva = obtenerReservaVigente(command.reservaRaizId());
        usuarioRepository.buscarPorId(command.usuarioId())
                .orElseThrow(() -> new DomainException("Usuario no encontrado"));
        ReservaSalon reservaDestino = obtenerReservaDestino(reserva, command.usuarioId());
        UUID menuId = UUID.randomUUID();

        if (command.selecciones() == null || command.selecciones().isEmpty()) {
            throw new DomainException("El menu debe tener al menos una seleccion");
        }

        List<SeleccionMenu> selecciones = command.selecciones().stream()
                .map(seleccion -> toDomain(menuId, seleccion))
                .toList();

        return toView(menuRepository.guardar(Menu.configurar(
                menuId,
                reservaDestino.getId(),
                command.notasGenerales(),
                selecciones
        )));
    }

    @Override
    public MenuView obtenerPorReservaRaizId(UUID reservaRaizId) {
        ReservaSalon reserva = obtenerReservaVigente(reservaRaizId);
        return menuRepository.buscarPorReservaId(reserva.getId())
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Menu no encontrado para la reserva indicada"));
    }

    private ReservaSalon obtenerReservaVigente(UUID reservaRaizId) {
        return reservaSalonRepository.buscarVigentePorRaizId(reservaRaizId)
                .orElseThrow(() -> new DomainException("No existe una reserva vigente para el identificador indicado"));
    }

    private ReservaSalon obtenerReservaDestino(ReservaSalon reservaActual, UUID usuarioId) {
        if (menuRepository.buscarPorReservaId(reservaActual.getId()).isEmpty()) {
            return reservaActual;
        }
        return reservaSnapshotService.crearNuevaVersionCopiandoComponentes(reservaActual, usuarioId, true, false);
    }

    private SeleccionMenu toDomain(UUID menuId, SeleccionMenuCommand command) {
        if (!tipoMomentoMenuRepository.existeActivoPorId(command.tipoMomentoId())) {
            throw new DomainException("El momento de menu no existe o esta inactivo");
        }
        if (command.items() == null || command.items().isEmpty()) {
            throw new DomainException("La seleccion de menu debe tener al menos un item");
        }
        UUID seleccionId = UUID.randomUUID();
        List<ItemMenu> items = command.items().stream()
                .map(item -> toDomain(seleccionId, command.tipoMomentoId(), item))
                .toList();
        return SeleccionMenu.nueva(seleccionId, menuId, command.tipoMomentoId(), items);
    }

    private ItemMenu toDomain(UUID seleccionId, UUID tipoMomentoId, ItemMenuCommand command) {
        if (!platoRepository.existeActivoParaMomento(command.platoId(), tipoMomentoId)) {
            throw new DomainException("El plato no existe, esta inactivo o no corresponde al momento de menu");
        }
        return ItemMenu.nuevo(
                seleccionId,
                command.platoId(),
                command.cantidad(),
                command.excepciones(),
                command.precioOverride()
        );
    }

    private MenuView toView(Menu menu) {
        return new MenuView(
                menu.getId(),
                menu.getReservaId(),
                menu.getNotasGenerales(),
                menu.getSelecciones().stream().map(this::toView).toList()
        );
    }

    private SeleccionMenuView toView(SeleccionMenu seleccion) {
        return new SeleccionMenuView(
                seleccion.getId(),
                seleccion.getTipoMomentoId(),
                seleccion.getItems().stream().map(this::toView).toList()
        );
    }

    private ItemMenuView toView(ItemMenu item) {
        return new ItemMenuView(
                item.getId(),
                item.getPlatoId(),
                item.getCantidad(),
                item.getExcepciones(),
                item.getPrecioOverride()
        );
    }
}
