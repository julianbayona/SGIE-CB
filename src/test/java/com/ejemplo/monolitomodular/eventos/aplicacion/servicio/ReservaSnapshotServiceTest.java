package com.ejemplo.monolitomodular.eventos.aplicacion.servicio;

import com.ejemplo.monolitomodular.cotizaciones.dominio.modelo.Cotizacion;
import com.ejemplo.monolitomodular.cotizaciones.dominio.puerto.salida.CotizacionRepository;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.menus.dominio.modelo.ItemMenu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.Menu;
import com.ejemplo.monolitomodular.menus.dominio.modelo.SeleccionMenu;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.MenuRepository;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.AdicionalEvento;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.InfraestructuraReserva;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.Montaje;
import com.ejemplo.monolitomodular.montajes.dominio.modelo.MontajeMesaReserva;
import com.ejemplo.monolitomodular.montajes.dominio.puerto.salida.MontajeRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservaSnapshotServiceTest {

    @Test
    void deberiaCrearNuevaVersionCopiandoMontajeYMenu() {
        UUID usuarioId = UUID.randomUUID();
        ReservaSalon reserva = ReservaSalon.nueva(
                UUID.randomUUID(),
                UUID.randomUUID(),
                80,
                LocalDateTime.of(2026, 9, 10, 8, 0),
                LocalDateTime.of(2026, 9, 10, 12, 0),
                usuarioId
        );
        ReservaSalonRepositoryStub reservaRepository = new ReservaSalonRepositoryStub(reserva);
        MontajeRepositoryStub montajeRepository = new MontajeRepositoryStub();
        MenuRepositoryStub menuRepository = new MenuRepositoryStub();
        CotizacionRepositoryStub cotizacionRepository = new CotizacionRepositoryStub();
        montajeRepository.guardar(montaje(reserva.getId()));
        menuRepository.guardar(menu(reserva.getId()));

        ReservaSnapshotService service = new ReservaSnapshotService(
                reservaRepository,
                montajeRepository,
                menuRepository,
                cotizacionRepository
        );

        ReservaSalon nueva = service.crearNuevaVersionCopiandoComponentes(reserva, usuarioId, true, true);

        assertEquals(2, nueva.getVersion());
        assertEquals(reserva.getReservaRaizId(), nueva.getReservaRaizId());
        assertEquals(2, reservaRepository.totalVersiones());
        assertEquals(nueva.getId(), montajeRepository.buscarPorReservaId(nueva.getId()).orElseThrow().getReservaId());
        assertEquals(nueva.getId(), menuRepository.buscarPorReservaId(nueva.getId()).orElseThrow().getReservaId());
        assertEquals(2, montajeRepository.totalMontajes());
        assertEquals(2, menuRepository.totalMenus());
        assertEquals(reserva.getId(), cotizacionRepository.reservaDesactualizada());
    }

    private static Montaje montaje(UUID reservaId) {
        UUID montajeId = UUID.randomUUID();
        return Montaje.configurar(
                montajeId,
                reservaId,
                "Montaje base",
                List.of(MontajeMesaReserva.nueva(
                        montajeId,
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        6,
                        10,
                        UUID.randomUUID(),
                        null,
                        true,
                        false
                )),
                InfraestructuraReserva.nueva(montajeId, false, true, false, false),
                List.of(AdicionalEvento.nuevo(montajeId, UUID.randomUUID(), 1))
        );
    }

    private static Menu menu(UUID reservaId) {
        UUID menuId = UUID.randomUUID();
        UUID seleccionId = UUID.randomUUID();
        return Menu.configurar(
                menuId,
                reservaId,
                "Sin lactosa",
                List.of(SeleccionMenu.nueva(
                        seleccionId,
                        menuId,
                        UUID.randomUUID(),
                        List.of(ItemMenu.nuevo(seleccionId, UUID.randomUUID(), 80, "Sin sal"))
                ))
        );
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

        int totalVersiones() {
            return reservas.size();
        }
    }

    private static class MontajeRepositoryStub implements MontajeRepository {

        private final List<Montaje> montajes = new ArrayList<>();

        @Override
        public Montaje guardar(Montaje montaje) {
            montajes.add(montaje);
            return montaje;
        }

        @Override
        public Optional<Montaje> buscarPorReservaId(UUID reservaId) {
            return montajes.stream().filter(montaje -> montaje.getReservaId().equals(reservaId)).findFirst();
        }

        int totalMontajes() {
            return montajes.size();
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
}
