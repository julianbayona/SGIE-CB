package com.ejemplo.monolitomodular.salones.aplicacion.servicio;

import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.salones.aplicacion.dto.ConsultarDisponibilidadSalonesQuery;
import com.ejemplo.monolitomodular.salones.aplicacion.dto.RegistrarSalonCommand;
import com.ejemplo.monolitomodular.salones.aplicacion.dto.SalonView;
import com.ejemplo.monolitomodular.salones.dominio.modelo.Salon;
import com.ejemplo.monolitomodular.salones.dominio.puerto.salida.SalonRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SalonApplicationServiceTest {

    @Test
    void deberiaRegistrarSalon() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        SalonView salon = service.ejecutar(new RegistrarSalonCommand("Salon Republica", 120, "Principal"));

        assertEquals("Salon Republica", salon.nombre());
        assertEquals(120, salon.capacidad());
    }

    @Test
    void noDeberiaPermitirNombresDuplicados() {
        InMemorySalonRepositoryStub repository = new InMemorySalonRepositoryStub();
        SalonApplicationService service = new SalonApplicationService(
                repository,
                new ReservaSalonRepositoryStub(Set.of())
        );
        service.ejecutar(new RegistrarSalonCommand("Salon Republica", 120, "Principal"));

        assertThrows(
                DomainException.class,
                () -> service.ejecutar(new RegistrarSalonCommand("Salon Republica", 80, "Auxiliar"))
        );
    }

    @Test
    void deberiaConsultarSalonesDisponiblesExcluyendoOcupados() {
        Salon salonLibre = Salon.nuevo("Salon Republica", 120, "Principal");
        Salon salonOcupado = Salon.nuevo("Salon Colonial", 80, "Segundo piso");
        InMemorySalonRepositoryStub repository = new InMemorySalonRepositoryStub();
        repository.guardar(salonLibre);
        repository.guardar(salonOcupado);

        SalonApplicationService service = new SalonApplicationService(
                repository,
                new ReservaSalonRepositoryStub(Set.of(salonOcupado.getId()))
        );

        List<SalonView> disponibles = service.consultarDisponibilidad(
                new ConsultarDisponibilidadSalonesQuery(
                        LocalDateTime.of(2026, 5, 10, 18, 0),
                        LocalDateTime.of(2026, 5, 10, 22, 0),
                        null
                )
        );

        assertEquals(1, disponibles.size());
        assertEquals(salonLibre.getId(), disponibles.get(0).id());
    }

    @Test
    void deberiaFiltrarPorCapacidadMinimaEnDisponibilidad() {
        Salon salonPequeno = Salon.nuevo("Salon Pequeno", 40, "Auxiliar");
        Salon salonGrande = Salon.nuevo("Salon Grande", 120, "Principal");
        InMemorySalonRepositoryStub repository = new InMemorySalonRepositoryStub();
        repository.guardar(salonPequeno);
        repository.guardar(salonGrande);

        SalonApplicationService service = new SalonApplicationService(
                repository,
                new ReservaSalonRepositoryStub(Set.of())
        );

        List<SalonView> disponibles = service.consultarDisponibilidad(
                new ConsultarDisponibilidadSalonesQuery(
                        LocalDateTime.of(2026, 5, 10, 18, 0),
                        LocalDateTime.of(2026, 5, 10, 22, 0),
                        80
                )
        );

        assertEquals(1, disponibles.size());
        assertEquals(salonGrande.getId(), disponibles.get(0).id());
    }

    private static class InMemorySalonRepositoryStub implements SalonRepository {

        private final List<Salon> salones = new ArrayList<>();

        @Override
        public Salon guardar(Salon salon) {
            salones.add(salon);
            return salon;
        }

        @Override
        public Optional<Salon> buscarPorId(UUID id) {
            return salones.stream().filter(salon -> salon.getId().equals(id)).findFirst();
        }

        @Override
        public List<Salon> listar() {
            return List.copyOf(salones);
        }

        @Override
        public List<Salon> buscarTodosPorIds(Collection<UUID> ids) {
            return salones.stream().filter(salon -> ids.contains(salon.getId())).toList();
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return salones.stream().anyMatch(salon -> salon.getNombre().equalsIgnoreCase(nombre));
        }
    }

    private static class ReservaSalonRepositoryStub implements ReservaSalonRepository {

        private final Set<UUID> salonesOcupados;

        private ReservaSalonRepositoryStub(Set<UUID> salonesOcupados) {
            this.salonesOcupados = salonesOcupados;
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
            return salonesOcupados.contains(salonId);
        }

        @Override
        public boolean existeConflicto(UUID salonId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, UUID reservaRaizIdExcluida) {
            return salonesOcupados.contains(salonId);
        }

        @Override
        public List<ReservaSalon> listarPorEvento(UUID eventoId) {
            return List.of();
        }

        @Override
        public Set<UUID> buscarSalonesOcupados(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
            return salonesOcupados;
        }

        @Override
        public Optional<ReservaSalon> buscarVigentePorEventoYSalon(UUID eventoId, UUID salonId) {
            return Optional.empty();
        }

        @Override
        public Optional<ReservaSalon> buscarVigentePorRaizId(UUID reservaRaizId) {
            return Optional.empty();
        }

        @Override
        public void desactivarReservaVigente(UUID reservaRaizId) {
        }
    }
}
