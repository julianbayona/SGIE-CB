package com.ejemplo.monolitomodular.montajes.aplicacion.servicio;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Mantel;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Sobremantel;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoAdicional;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoMesa;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoSilla;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.MantelRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.SobremantelRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoAdicionalRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoMesaRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoSillaRepository;
import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.servicio.ReservaSnapshotService;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.AdicionalEventoCommand;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Menu;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.MenuRepository;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.ConfigurarMontajeCommand;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.InfraestructuraReservaCommand;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.MontajeMesaReservaCommand;
import com.ejemplo.monolitomodular.montajes.aplicacion.dto.MontajeView;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.Montaje;
import com.ejemplo.monolitomodular.montajes.dominio.puerto.salida.MontajeRepository;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MontajeApplicationServiceTest {

    @Test
    void deberiaConfigurarMontajeParaReservaVigente() {
        UUID tipoMesaId = UUID.randomUUID();
        UUID tipoSillaId = UUID.randomUUID();
        UUID mantelId = UUID.randomUUID();
        UUID sobremantelId = UUID.randomUUID();
        UUID tipoAdicionalId = UUID.randomUUID();
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        ReservaSalon reserva = ReservaSalon.nueva(
                UUID.randomUUID(),
                UUID.randomUUID(),
                80,
                LocalDateTime.of(2026, 9, 10, 8, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                UUID.randomUUID()
        );

        ReservaSalonRepositoryStub reservaRepository = new ReservaSalonRepositoryStub(reserva);
        MontajeRepositoryStub montajeRepository = new MontajeRepositoryStub();
        MenuRepositoryStub menuRepository = new MenuRepositoryStub();
        MontajeApplicationService service = new MontajeApplicationService(
                reservaRepository,
                new TipoMesaRepositoryStub(Set.of(tipoMesaId)),
                new TipoSillaRepositoryStub(Set.of(tipoSillaId)),
                new MantelRepositoryStub(Set.of(mantelId)),
                new SobremantelRepositoryStub(Set.of(sobremantelId)),
                new TipoAdicionalRepositoryStub(Set.of(tipoAdicionalId)),
                montajeRepository,
                new UsuarioRepositoryStub(usuario),
                new ReservaSnapshotService(reservaRepository, montajeRepository, menuRepository, new CotizacionRepositoryStub())
        );

        MontajeView montaje = service.ejecutar(new ConfigurarMontajeCommand(
                reserva.getReservaRaizId(),
                usuario.getId(),
                "Montaje tipo conferencia",
                List.of(new MontajeMesaReservaCommand(
                        tipoMesaId,
                        tipoSillaId,
                        6,
                        10,
                        mantelId,
                        sobremantelId,
                        true,
                        false
                )),
                new InfraestructuraReservaCommand(false, false, true, false),
                List.of(new AdicionalEventoCommand(tipoAdicionalId, 1))
        ));

        assertEquals(reserva.getId(), montaje.reservaId());
        assertEquals(1, montaje.mesas().size());
        assertEquals(10, montaje.mesas().get(0).cantidadMesas());
        assertEquals(true, montaje.infraestructura().espacioMusicos());
        assertEquals(1, montaje.adicionales().size());
        assertEquals(1, montaje.adicionales().get(0).cantidad());
    }

    @Test
    void deberiaCrearNuevaVersionDeReservaCuandoSeModificaMontajeExistente() {
        UUID tipoMesaId = UUID.randomUUID();
        UUID tipoSillaId = UUID.randomUUID();
        UUID mantelId = UUID.randomUUID();
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        ReservaSalon reserva = ReservaSalon.nueva(
                UUID.randomUUID(),
                UUID.randomUUID(),
                80,
                LocalDateTime.of(2026, 9, 10, 8, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                usuario.getId()
        );
        ReservaSalonRepositoryStub reservaRepository = new ReservaSalonRepositoryStub(reserva);
        MontajeRepositoryStub montajeRepository = new MontajeRepositoryStub();
        MontajeApplicationService service = new MontajeApplicationService(
                reservaRepository,
                new TipoMesaRepositoryStub(Set.of(tipoMesaId)),
                new TipoSillaRepositoryStub(Set.of(tipoSillaId)),
                new MantelRepositoryStub(Set.of(mantelId)),
                new SobremantelRepositoryStub(Set.of()),
                new TipoAdicionalRepositoryStub(Set.of()),
                montajeRepository,
                new UsuarioRepositoryStub(usuario),
                new ReservaSnapshotService(reservaRepository, montajeRepository, new MenuRepositoryStub(), new CotizacionRepositoryStub())
        );

        MontajeView montajeInicial = service.ejecutar(new ConfigurarMontajeCommand(
                reserva.getReservaRaizId(),
                usuario.getId(),
                "Montaje inicial",
                List.of(new MontajeMesaReservaCommand(tipoMesaId, tipoSillaId, 6, 10, mantelId, null, true, false)),
                new InfraestructuraReservaCommand(false, false, true, false),
                List.of()
        ));
        MontajeView montajeModificado = service.ejecutar(new ConfigurarMontajeCommand(
                reserva.getReservaRaizId(),
                usuario.getId(),
                "Montaje modificado",
                List.of(new MontajeMesaReservaCommand(tipoMesaId, tipoSillaId, 6, 12, mantelId, null, true, false)),
                new InfraestructuraReservaCommand(false, true, true, false),
                List.of()
        ));

        assertEquals(2, reservaRepository.totalVersiones());
        assertEquals(2, reservaRepository.reservaVigente().getVersion());
        assertEquals(reserva.getReservaRaizId(), reservaRepository.reservaVigente().getReservaRaizId());
        assertEquals(reserva.getId(), montajeInicial.reservaId());
        assertEquals(reservaRepository.reservaVigente().getId(), montajeModificado.reservaId());
        assertEquals(2, montajeRepository.totalMontajes());
    }

    private static class ReservaSalonRepositoryStub implements ReservaSalonRepository {

        private final List<ReservaSalon> reservas = new java.util.ArrayList<>();

        private ReservaSalonRepositoryStub(ReservaSalon reserva) {
            this.reservas.add(reserva);
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

    private static class TipoMesaRepositoryStub implements TipoMesaRepository {

        private final Set<UUID> activos;

        private TipoMesaRepositoryStub(Set<UUID> activos) {
            this.activos = activos;
        }

        @Override
        public TipoMesa guardar(TipoMesa tipoMesa) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<TipoMesa> buscarPorId(UUID id) {
            return Optional.empty();
        }

        @Override
        public List<TipoMesa> listar() {
            return List.of();
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return activos.contains(id);
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return false;
        }
    }

    private static class TipoSillaRepositoryStub implements TipoSillaRepository {

        private final Set<UUID> activos;

        private TipoSillaRepositoryStub(Set<UUID> activos) {
            this.activos = activos;
        }

        @Override
        public TipoSilla guardar(TipoSilla tipoSilla) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<TipoSilla> buscarPorId(UUID id) {
            return Optional.empty();
        }

        @Override
        public List<TipoSilla> listar() {
            return List.of();
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return activos.contains(id);
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return false;
        }
    }

    private static class MantelRepositoryStub implements MantelRepository {

        private final Set<UUID> activos;

        private MantelRepositoryStub(Set<UUID> activos) {
            this.activos = activos;
        }

        @Override
        public Mantel guardar(Mantel mantel) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Mantel> buscarPorId(UUID id) {
            return Optional.empty();
        }

        @Override
        public List<Mantel> listar() {
            return List.of();
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return activos.contains(id);
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return false;
        }
    }

    private static class SobremantelRepositoryStub implements SobremantelRepository {

        private final Set<UUID> activos;

        private SobremantelRepositoryStub(Set<UUID> activos) {
            this.activos = activos;
        }

        @Override
        public Sobremantel guardar(Sobremantel sobremantel) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Sobremantel> buscarPorId(UUID id) {
            return Optional.empty();
        }

        @Override
        public List<Sobremantel> listar() {
            return List.of();
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return activos.contains(id);
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return false;
        }
    }

    private static class TipoAdicionalRepositoryStub implements TipoAdicionalRepository {

        private final Set<UUID> activos;

        private TipoAdicionalRepositoryStub(Set<UUID> activos) {
            this.activos = activos;
        }

        @Override
        public TipoAdicional guardar(TipoAdicional tipoAdicional) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<TipoAdicional> buscarPorId(UUID id) {
            return Optional.empty();
        }

        @Override
        public List<TipoAdicional> listar() {
            return List.of();
        }

        @Override
        public boolean existeActivoPorId(UUID id) {
            return activos.contains(id);
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return false;
        }
    }

    private static class MontajeRepositoryStub implements MontajeRepository {

        private final List<Montaje> montajes = new java.util.ArrayList<>();

        @Override
        public Montaje guardar(Montaje montaje) {
            montajes.add(montaje);
            return montaje;
        }

        @Override
        public Optional<Montaje> buscarPorReservaId(UUID reservaId) {
            return montajes.stream()
                    .filter(montaje -> montaje.getReservaId().equals(reservaId))
                    .findFirst();
        }

        int totalMontajes() {
            return montajes.size();
        }
    }

    private static class MenuRepositoryStub implements MenuRepository {

        @Override
        public Menu guardar(Menu menu) {
            return menu;
        }

        @Override
        public Optional<Menu> buscarPorReservaId(UUID reservaId) {
            return Optional.empty();
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
            if (usuario.getId().equals(id)) {
                return Optional.of(usuario);
            }
            return Optional.empty();
        }
    }

    private static class CotizacionRepositoryStub implements CotizacionRepository {

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
        }
    }
}
