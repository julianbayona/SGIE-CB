package com.ejemplo.monolitomodular.cotizaciones.aplicacion.servicio;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.ModoCobroAdicional;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoAdicional;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoAdicionalRepository;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.ActualizarItemCotizacionCommand;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.CotizacionView;
import com.ejemplo.monolitomodular.cotizaciones.aplicacion.dto.GenerarCotizacionCommand;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.EstadoCotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.menus.dominio.modelo.ItemMenu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Menu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Plato;
import com.ejemplo.monolitomodular.menus.dominio.modelo.SeleccionMenu;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.MenuRepository;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.PlatoRepository;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.AdicionalEvento;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.InfraestructuraReserva;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.Montaje;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.MontajeMesaReserva;
import com.ejemplo.monolitomodular.montajes.dominio.puerto.salida.MontajeRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CotizacionApplicationServiceTest {

    @Test
    void deberiaGenerarCotizacionParaReservaVigente() {
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        ReservaSalon reserva = reserva(usuario.getId());
        CotizacionRepositoryStub cotizacionRepository = new CotizacionRepositoryStub();
        UUID platoId = UUID.randomUUID();
        UUID tipoAdicionalId = UUID.randomUUID();
        CotizacionApplicationService service = new CotizacionApplicationService(
                new ReservaSalonRepositoryStub(reserva),
                new UsuarioRepositoryStub(usuario),
                cotizacionRepository,
                new MenuRepositoryStub(menu(reserva.getId(), platoId, 80)),
                new MontajeRepositoryStub(montaje(reserva.getId(), tipoAdicionalId)),
                new PlatoRepositoryStub(Plato.reconstruir(platoId, "Almuerzo ejecutivo", null, new BigDecimal("25000.00"), true)),
                new TipoAdicionalRepositoryStub(TipoAdicional.reconstruir(
                        tipoAdicionalId,
                        "Sonido",
                        ModoCobroAdicional.SERVICIO,
                        new BigDecimal("120000.00"),
                        true
                ))
        );

        CotizacionView cotizacion = service.ejecutar(command(reserva.getReservaRaizId(), usuario.getId()));

        assertEquals(reserva.getId(), cotizacion.reservaId());
        assertEquals(EstadoCotizacion.BORRADOR, cotizacion.estado());
        assertEquals(new BigDecimal("2120000.00"), cotizacion.valorSubtotal());
        assertEquals(new BigDecimal("120000.00"), cotizacion.descuento());
        assertEquals(new BigDecimal("2000000.00"), cotizacion.valorTotal());
        assertEquals(2, cotizacion.items().size());
        assertEquals(1, cotizacionRepository.totalCotizaciones());
    }

    @Test
    void noDeberiaGenerarCotizacionSiYaExisteUnaActivaParaLaReservaVigente() {
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        ReservaSalon reserva = reserva(usuario.getId());
        CotizacionRepositoryStub cotizacionRepository = new CotizacionRepositoryStub();
        UUID platoId = UUID.randomUUID();
        UUID tipoAdicionalId = UUID.randomUUID();
        CotizacionApplicationService service = new CotizacionApplicationService(
                new ReservaSalonRepositoryStub(reserva),
                new UsuarioRepositoryStub(usuario),
                cotizacionRepository,
                new MenuRepositoryStub(menu(reserva.getId(), platoId, 80)),
                new MontajeRepositoryStub(montaje(reserva.getId(), tipoAdicionalId)),
                new PlatoRepositoryStub(Plato.reconstruir(platoId, "Almuerzo ejecutivo", null, new BigDecimal("25000.00"), true)),
                new TipoAdicionalRepositoryStub(TipoAdicional.reconstruir(
                        tipoAdicionalId,
                        "Sonido",
                        ModoCobroAdicional.SERVICIO,
                        new BigDecimal("120000.00"),
                        true
                ))
        );
        service.ejecutar(command(reserva.getReservaRaizId(), usuario.getId()));

        assertThrows(DomainException.class, () -> service.ejecutar(command(reserva.getReservaRaizId(), usuario.getId())));
    }

    @Test
    void deberiaActualizarPrecioOverrideDeItemEnBorrador() {
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        ReservaSalon reserva = reserva(usuario.getId());
        CotizacionRepositoryStub cotizacionRepository = new CotizacionRepositoryStub();
        UUID platoId = UUID.randomUUID();
        UUID tipoAdicionalId = UUID.randomUUID();
        CotizacionApplicationService service = new CotizacionApplicationService(
                new ReservaSalonRepositoryStub(reserva),
                new UsuarioRepositoryStub(usuario),
                cotizacionRepository,
                new MenuRepositoryStub(menu(reserva.getId(), platoId, 80)),
                new MontajeRepositoryStub(montaje(reserva.getId(), tipoAdicionalId)),
                new PlatoRepositoryStub(Plato.reconstruir(platoId, "Almuerzo ejecutivo", null, new BigDecimal("25000.00"), true)),
                new TipoAdicionalRepositoryStub(TipoAdicional.reconstruir(
                        tipoAdicionalId,
                        "Sonido",
                        ModoCobroAdicional.SERVICIO,
                        new BigDecimal("120000.00"),
                        true
                ))
        );
        CotizacionView borrador = service.ejecutar(command(reserva.getReservaRaizId(), usuario.getId()));
        UUID itemMenuId = borrador.items().stream()
                .filter(item -> item.tipoConcepto().equals("MENU"))
                .findFirst()
                .orElseThrow()
                .id();

        CotizacionView ajustada = service.ejecutar(new ActualizarItemCotizacionCommand(
                borrador.id(),
                itemMenuId,
                new BigDecimal("23000.00")
        ));

        assertEquals(new BigDecimal("1960000.00"), ajustada.valorSubtotal());
        assertEquals(new BigDecimal("1840000.00"), ajustada.valorTotal());
        assertEquals(new BigDecimal("23000.00"), ajustada.items().stream()
                .filter(item -> item.id().equals(itemMenuId))
                .findFirst()
                .orElseThrow()
                .precioOverride());
    }

    @Test
    void deberiaCopiarPrecioNegociadoCuandoSeGeneraBorradorParaNuevaVersionDeReserva() {
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        ReservaSalon reservaV1 = reserva(usuario.getId());
        ReservaSalon reservaV2 = reservaV1.crearNuevaVersion(
                reservaV1.getSalonId(),
                95,
                reservaV1.getFechaHoraInicio(),
                reservaV1.getFechaHoraFin(),
                usuario.getId()
        );
        CotizacionRepositoryStub cotizacionRepository = new CotizacionRepositoryStub();
        UUID platoId = UUID.randomUUID();
        UUID tipoAdicionalId = UUID.randomUUID();
        Plato plato = Plato.reconstruir(platoId, "Almuerzo ejecutivo", null, new BigDecimal("25000.00"), true);
        TipoAdicional tipoAdicional = TipoAdicional.reconstruir(
                tipoAdicionalId,
                "Sonido",
                ModoCobroAdicional.SERVICIO,
                new BigDecimal("120000.00"),
                true
        );
        CotizacionApplicationService serviceV1 = new CotizacionApplicationService(
                new ReservaSalonRepositoryStub(reservaV1),
                new UsuarioRepositoryStub(usuario),
                cotizacionRepository,
                new MenuRepositoryStub(menu(reservaV1.getId(), platoId, 80)),
                new MontajeRepositoryStub(montaje(reservaV1.getId(), tipoAdicionalId)),
                new PlatoRepositoryStub(plato),
                new TipoAdicionalRepositoryStub(tipoAdicional)
        );
        CotizacionView borradorV1 = serviceV1.ejecutar(command(reservaV1.getReservaRaizId(), usuario.getId()));
        UUID itemMenuId = borradorV1.items().stream()
                .filter(item -> item.tipoConcepto().equals("MENU"))
                .findFirst()
                .orElseThrow()
                .id();
        serviceV1.ejecutar(new ActualizarItemCotizacionCommand(
                borradorV1.id(),
                itemMenuId,
                new BigDecimal("23000.00")
        ));

        CotizacionApplicationService serviceV2 = new CotizacionApplicationService(
                new ReservaSalonRepositoryStub(reservaV2),
                new UsuarioRepositoryStub(usuario),
                cotizacionRepository,
                new MenuRepositoryStub(menu(reservaV2.getId(), platoId, 95)),
                new MontajeRepositoryStub(montaje(reservaV2.getId(), tipoAdicionalId)),
                new PlatoRepositoryStub(plato),
                new TipoAdicionalRepositoryStub(tipoAdicional)
        );

        CotizacionView borradorV2 = serviceV2.ejecutar(command(reservaV2.getReservaRaizId(), usuario.getId()));

        assertEquals(reservaV2.getId(), borradorV2.reservaId());
        assertEquals(new BigDecimal("23000.00"), borradorV2.items().stream()
                .filter(item -> item.tipoConcepto().equals("MENU"))
                .findFirst()
                .orElseThrow()
                .precioOverride());
        assertEquals(new BigDecimal("2305000.00"), borradorV2.valorSubtotal());
        assertEquals(new BigDecimal("2185000.00"), borradorV2.valorTotal());
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

    private static GenerarCotizacionCommand command(UUID reservaRaizId, UUID usuarioId) {
        return new GenerarCotizacionCommand(
                reservaRaizId,
                usuarioId,
                new BigDecimal("120000.00"),
                "Cotizacion inicial"
        );
    }

    private static Menu menu(UUID reservaId, UUID platoId, int cantidad) {
        UUID menuId = UUID.randomUUID();
        UUID seleccionId = UUID.randomUUID();
        return Menu.configurar(
                menuId,
                reservaId,
                null,
                List.of(SeleccionMenu.nueva(
                        seleccionId,
                        menuId,
                        UUID.randomUUID(),
                        List.of(ItemMenu.nuevo(seleccionId, platoId, cantidad, null))
                ))
        );
    }

    private static Montaje montaje(UUID reservaId, UUID tipoAdicionalId) {
        UUID montajeId = UUID.randomUUID();
        return Montaje.configurar(
                montajeId,
                reservaId,
                null,
                List.of(MontajeMesaReserva.nueva(
                        montajeId,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        8,
                        10,
                        UUID.randomUUID(),
                        null,
                        true,
                        false
                )),
                InfraestructuraReserva.nueva(montajeId, false, false, true, false),
                List.of(AdicionalEvento.nuevo(montajeId, tipoAdicionalId, 5))
        );
    }

    private static class ReservaSalonRepositoryStub implements ReservaSalonRepository {

        private final ReservaSalon reserva;

        private ReservaSalonRepositoryStub(ReservaSalon reserva) {
            this.reserva = reserva;
        }

        @Override
        public List<ReservaSalon> guardarTodas(List<ReservaSalon> reservas) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ReservaSalon guardar(ReservaSalon reserva) {
            throw new UnsupportedOperationException();
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
        public Optional<ReservaSalon> buscarVigentePorRaizId(UUID reservaRaizId) {
            if (reserva.isVigente() && reserva.getReservaRaizId().equals(reservaRaizId)) {
                return Optional.of(reserva);
            }
            return Optional.empty();
        }

        @Override
        public void desactivarReservaVigente(UUID reservaRaizId) {
            throw new UnsupportedOperationException();
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

    private static class CotizacionRepositoryStub implements CotizacionRepository {

        private final List<Cotizacion> cotizaciones = new ArrayList<>();

        @Override
        public Cotizacion guardar(Cotizacion cotizacion) {
            cotizaciones.removeIf(actual -> actual.getId().equals(cotizacion.getId()));
            cotizaciones.add(cotizacion);
            return cotizacion;
        }

        @Override
        public Optional<Cotizacion> buscarPorId(UUID id) {
            return cotizaciones.stream().filter(cotizacion -> cotizacion.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<Cotizacion> buscarActivaPorReservaId(UUID reservaId) {
            return cotizaciones.stream()
                    .filter(cotizacion -> cotizacion.getReservaId().equals(reservaId))
                    .filter(cotizacion -> cotizacion.getEstado() != EstadoCotizacion.RECHAZADA)
                    .filter(cotizacion -> cotizacion.getEstado() != EstadoCotizacion.DESACTUALIZADA)
                    .findFirst();
        }

        @Override
        public Optional<Cotizacion> buscarUltimaPorReservaRaizId(UUID reservaRaizId) {
            if (cotizaciones.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(cotizaciones.get(cotizaciones.size() - 1));
        }

        @Override
        public void desactualizarActivasPorReservaId(UUID reservaId) {
        }

        int totalCotizaciones() {
            return cotizaciones.size();
        }
    }

    private static class MenuRepositoryStub implements MenuRepository {

        private final Menu menu;

        private MenuRepositoryStub(Menu menu) {
            this.menu = menu;
        }

        @Override
        public Menu guardar(Menu menu) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Menu> buscarPorReservaId(UUID reservaId) {
            return menu.getReservaId().equals(reservaId) ? Optional.of(menu) : Optional.empty();
        }
    }

    private static class MontajeRepositoryStub implements MontajeRepository {

        private final Montaje montaje;

        private MontajeRepositoryStub(Montaje montaje) {
            this.montaje = montaje;
        }

        @Override
        public Montaje guardar(Montaje montaje) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Montaje> buscarPorReservaId(UUID reservaId) {
            return montaje.getReservaId().equals(reservaId) ? Optional.of(montaje) : Optional.empty();
        }
    }

    private static class PlatoRepositoryStub implements PlatoRepository {

        private final Plato plato;

        private PlatoRepositoryStub(Plato plato) {
            this.plato = plato;
        }

        @Override
        public Optional<Plato> buscarPorId(UUID id) {
            return plato.getId().equals(id) ? Optional.of(plato) : Optional.empty();
        }

        @Override
        public boolean existeActivoParaMomento(UUID platoId, UUID tipoMomentoId) {
            return false;
        }
    }

    private static class TipoAdicionalRepositoryStub implements TipoAdicionalRepository {

        private final TipoAdicional tipoAdicional;

        private TipoAdicionalRepositoryStub(TipoAdicional tipoAdicional) {
            this.tipoAdicional = tipoAdicional;
        }

        @Override
        public TipoAdicional guardar(TipoAdicional tipoAdicional) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<TipoAdicional> buscarPorId(UUID id) {
            return tipoAdicional.getId().equals(id) ? Optional.of(tipoAdicional) : Optional.empty();
        }

        @Override
        public List<TipoAdicional> listar() {
            return List.of();
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return false;
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return false;
        }
    }
}
