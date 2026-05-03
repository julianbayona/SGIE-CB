package com.ejemplo.monolitomodular.menus.aplicacion.servicio;

import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.servicio.ReservaSnapshotService;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.HistorialEstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.HistorialEstadoEventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.ConfigurarMenuCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.ItemMenuCommand;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.MenuView;
import com.ejemplo.monolitomodular.menus.aplicacion.dto.SeleccionMenuCommand;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Menu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Plato;
import com.ejemplo.monolitomodular.menus.dominio.modelo.TipoMomentoMenu;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.MenuRepository;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.PlatoRepository;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.TipoMomentoMenuRepository;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.Montaje;
import com.ejemplo.monolitomodular.montajes.dominio.puerto.salida.MontajeRepository;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MenuApplicationServiceTest {

    @Test
    void deberiaConfigurarMenuParaReservaVigente() {
        UUID tipoMomentoId = UUID.randomUUID();
        UUID platoId = UUID.randomUUID();
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        ReservaSalon reserva = reserva(usuario.getId());

        ReservaSalonRepositoryStub reservaRepository = new ReservaSalonRepositoryStub(reserva);
        MenuRepositoryStub menuRepository = new MenuRepositoryStub();
        MontajeRepositoryStub montajeRepository = new MontajeRepositoryStub();
        MenuApplicationService service = new MenuApplicationService(
                reservaRepository,
                new UsuarioRepositoryStub(usuario),
                menuRepository,
                new TipoMomentoMenuRepositoryStub(Set.of(tipoMomentoId)),
                new PlatoRepositoryStub(Set.of(new PlatoMomento(platoId, tipoMomentoId))),
                snapshotService(reservaRepository, montajeRepository, menuRepository, new CotizacionRepositoryStub(), reserva)
        );

        MenuView menu = service.ejecutar(command(reserva.getReservaRaizId(), usuario.getId(), tipoMomentoId, platoId, 80));

        assertEquals(reserva.getId(), menu.reservaId());
        assertEquals(1, menu.selecciones().size());
        assertEquals(80, menu.selecciones().get(0).items().get(0).cantidad());
    }

    @Test
    void deberiaCrearNuevaVersionDeReservaCuandoSeModificaMenuExistente() {
        UUID tipoMomentoId = UUID.randomUUID();
        UUID platoId = UUID.randomUUID();
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        ReservaSalon reserva = reserva(usuario.getId());
        ReservaSalonRepositoryStub reservaRepository = new ReservaSalonRepositoryStub(reserva);
        MenuRepositoryStub menuRepository = new MenuRepositoryStub();
        CotizacionRepositoryStub cotizacionRepository = new CotizacionRepositoryStub();
        MenuApplicationService service = new MenuApplicationService(
                reservaRepository,
                new UsuarioRepositoryStub(usuario),
                menuRepository,
                new TipoMomentoMenuRepositoryStub(Set.of(tipoMomentoId)),
                new PlatoRepositoryStub(Set.of(new PlatoMomento(platoId, tipoMomentoId))),
                snapshotService(reservaRepository, new MontajeRepositoryStub(), menuRepository, cotizacionRepository, reserva)
        );

        MenuView inicial = service.ejecutar(command(reserva.getReservaRaizId(), usuario.getId(), tipoMomentoId, platoId, 80));
        MenuView modificado = service.ejecutar(command(reserva.getReservaRaizId(), usuario.getId(), tipoMomentoId, platoId, 95));

        assertEquals(2, reservaRepository.totalVersiones());
        assertEquals(2, reservaRepository.reservaVigente().getVersion());
        assertEquals(reserva.getId(), inicial.reservaId());
        assertEquals(reservaRepository.reservaVigente().getId(), modificado.reservaId());
        assertEquals(2, menuRepository.totalMenus());
        assertEquals(reserva.getId(), cotizacionRepository.reservaDesactualizada());
    }

    private static ReservaSalon reserva(UUID usuarioId) {
        return ReservaSalon.nueva(
                UUID.randomUUID(),
                UUID.randomUUID(),
                80,
                LocalDateTime.of(2026, 9, 10, 8, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                usuarioId
        );
    }

    private static ConfigurarMenuCommand command(UUID reservaRaizId, UUID usuarioId, UUID tipoMomentoId, UUID platoId, int cantidad) {
        return new ConfigurarMenuCommand(
                reservaRaizId,
                usuarioId,
                "Sin lactosa",
                List.of(new SeleccionMenuCommand(
                        tipoMomentoId,
                        List.of(new ItemMenuCommand(platoId, cantidad, "Sin sal"))
                ))
        );
    }

    private static ReservaSnapshotService snapshotService(
            ReservaSalonRepository reservaRepository,
            MontajeRepository montajeRepository,
            MenuRepository menuRepository,
            CotizacionRepository cotizacionRepository,
            ReservaSalon reserva
    ) {
        return new ReservaSnapshotService(
                reservaRepository,
                montajeRepository,
                menuRepository,
                cotizacionRepository,
                new EventoRepositoryStub(evento(reserva)),
                new HistorialRepositoryStub()
        );
    }

    private static Evento evento(ReservaSalon reserva) {
        return Evento.reconstruir(
                reserva.getEventoId(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                reserva.getCreadoPor(),
                reserva.getFechaHoraInicio(),
                reserva.getFechaHoraFin(),
                EstadoEvento.PENDIENTE,
                null
        );
    }

    private record PlatoMomento(UUID platoId, UUID tipoMomentoId) {
    }

    private static class ReservaSalonRepositoryStub implements ReservaSalonRepository {

        private final List<ReservaSalon> reservas = new ArrayList<>();

        private ReservaSalonRepositoryStub(ReservaSalon reserva) {
            reservas.add(reserva);
        }

        @Override
        public List<ReservaSalon> guardarTodas(List<ReservaSalon> reservas) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReservaSalon guardar(ReservaSalon reserva) {
            reservas.removeIf(actual -> actual.getId().equals(reserva.getId()));
            reservas.add(reserva);
            return reserva;
        }

        @Override
        public boolean existeConflicto(UUID salonId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
            return false;
        }

        @Override
        public boolean existeConflicto(UUID salonId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, UUID reservaRaizIdExcluida) {
            return false;
        }

        @Override
        public List<ReservaSalon> listarPorEvento(UUID eventoId) {
            return List.of();
        }

        @Override
        public Set<UUID> buscarSalonesOcupados(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
            return Set.of();
        }

        @Override
        public Optional<ReservaSalon> buscarVigentePorEventoYSalon(UUID eventoId, UUID salonId) {
            return Optional.empty();
        }

        @Override
        public Optional<ReservaSalon> buscarPorId(UUID id) {
            return reservas.stream().filter(reserva -> reserva.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<ReservaSalon> buscarVigentePorRaizId(UUID reservaRaizId) {
            return reservas.stream()
                    .filter(ReservaSalon::isVigente)
                    .filter(reserva -> reserva.getReservaRaizId().equals(reservaRaizId))
                    .findFirst();
        }

        @Override
        public void desactivarReservaVigente(UUID reservaRaizId) {
            for (int i = 0; i < reservas.size(); i++) {
                ReservaSalon actual = reservas.get(i);
                if (actual.isVigente() && actual.getReservaRaizId().equals(reservaRaizId)) {
                    reservas.set(i, actual.marcarComoNoVigente());
                }
            }
        }

        ReservaSalon reservaVigente() {
            return reservas.stream().filter(ReservaSalon::isVigente).findFirst().orElseThrow();
        }

        int totalVersiones() {
            return reservas.size();
        }
    }

    private static class UsuarioRepositoryStub implements UsuarioRepository {

        private final Usuario usuario;

        private UsuarioRepositoryStub(Usuario usuario) {
            this.usuario = usuario;
        }

        @Override
        public Usuario guardar(Usuario usuario) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Usuario> buscarPorId(UUID id) {
            return usuario.getId().equals(id) ? Optional.of(usuario) : Optional.empty();
        }
    }

    private static class MenuRepositoryStub implements MenuRepository {

        private final List<Menu> menus = new ArrayList<>();

        @Override
        public Menu guardar(Menu menu) {
            menus.add(menu);
            return menu;
        }

        @Override
        public Optional<Menu> buscarPorReservaId(UUID reservaId) {
            return menus.stream().filter(menu -> menu.getReservaId().equals(reservaId)).findFirst();
        }

        int totalMenus() {
            return menus.size();
        }
    }

    private static class MontajeRepositoryStub implements MontajeRepository {

        @Override
        public Montaje guardar(Montaje montaje) {
            return montaje;
        }

        @Override
        public Optional<Montaje> buscarPorReservaId(UUID reservaId) {
            return Optional.empty();
        }
    }

    private static class TipoMomentoMenuRepositoryStub implements TipoMomentoMenuRepository {

        private final Set<UUID> activos;

        private TipoMomentoMenuRepositoryStub(Set<UUID> activos) {
            this.activos = activos;
        }

        @Override
        public Optional<TipoMomentoMenu> buscarPorId(UUID id) {
            return Optional.empty();
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return activos.contains(id);
        }
    }

    private static class PlatoRepositoryStub implements PlatoRepository {

        private final Set<PlatoMomento> activos;

        private PlatoRepositoryStub(Set<PlatoMomento> activos) {
            this.activos = activos;
        }

        @Override
        public Optional<Plato> buscarPorId(UUID id) {
            return Optional.empty();
        }

        @Override
        public boolean existeActivoParaMomento(UUID platoId, UUID tipoMomentoId) {
            return activos.contains(new PlatoMomento(platoId, tipoMomentoId));
        }
    }

    private static class CotizacionRepositoryStub implements CotizacionRepository {

        private UUID reservaDesactualizada;

        @Override
        public Cotizacion guardar(Cotizacion cotizacion) {
            return cotizacion;
        }

        @Override
        public Optional<Cotizacion> buscarPorId(UUID id) {
            return Optional.empty();
        }

        @Override
        public Optional<Cotizacion> buscarActivaPorReservaId(UUID reservaId) {
            return Optional.empty();
        }

        @Override
        public Optional<Cotizacion> buscarUltimaPorReservaRaizId(UUID reservaRaizId) {
            return Optional.empty();
        }

        @Override
        public void desactualizarActivasPorReservaId(UUID reservaId) {
            reservaDesactualizada = reservaId;
        }

        UUID reservaDesactualizada() {
            return reservaDesactualizada;
        }
    }

    private static class EventoRepositoryStub implements EventoRepository {

        private Evento evento;

        private EventoRepositoryStub(Evento evento) {
            this.evento = evento;
        }

        @Override
        public Evento guardar(Evento evento) {
            this.evento = evento;
            return evento;
        }

        @Override
        public Optional<Evento> buscarPorId(UUID id) {
            return evento.getId().equals(id) ? Optional.of(evento) : Optional.empty();
        }

        @Override
        public List<Evento> listar() {
            return List.of(evento);
        }
    }

    private static class HistorialRepositoryStub implements HistorialEstadoEventoRepository {

        @Override
        public HistorialEstadoEvento guardar(HistorialEstadoEvento historialEstadoEvento) {
            return historialEstadoEvento;
        }
    }
}
