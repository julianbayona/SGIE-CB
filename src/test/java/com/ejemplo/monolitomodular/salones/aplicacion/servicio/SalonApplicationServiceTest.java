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

    

    // --- Validaciones para RegistrarSalonCommand ---
    
    @Test
    void noDeberiaRegistrarSalonConNombreNull() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        assertThrows(
                DomainException.class,
                () -> service.ejecutar(new RegistrarSalonCommand(null, 50, "Test"))
        );
    }

    @Test
    void noDeberiaRegistrarSalonConNombreBlank() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        assertThrows(
                DomainException.class,
                () -> service.ejecutar(new RegistrarSalonCommand("   ", 50, "Test"))
        );
    }

    @Test
    void noDeberiaRegistrarSalonConCapacidadCero() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        assertThrows(
                DomainException.class,
                () -> service.ejecutar(new RegistrarSalonCommand("Salon Test", 0, "Test"))
        );
    }

    @Test
    void noDeberiaRegistrarSalonConCapacidadNegativa() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        assertThrows(
                DomainException.class,
                () -> service.ejecutar(new RegistrarSalonCommand("Salon Test", -10, "Test"))
        );
    }

    @Test
    void deberiaRegistrarSalonConDescripcionNull() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        SalonView salon = service.ejecutar(new RegistrarSalonCommand("Salon Test", 80, null));

        assertEquals("Salon Test", salon.nombre());
        assertEquals(80, salon.capacidad());
        assertEquals("", salon.descripcion());
    }

    @Test
    void deberiaRegistrarSalonConDescripcionVacia() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        SalonView salon = service.ejecutar(new RegistrarSalonCommand("Salon Test", 80, ""));

        assertEquals("Salon Test", salon.nombre());
        assertEquals("", salon.descripcion());
    }

    @Test
    void deberiaRegistrarSalonConCapacidadMaxima() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        SalonView salon = service.ejecutar(new RegistrarSalonCommand("Gran Auditorio", 10000, "Auditorio"));

        assertEquals("Gran Auditorio", salon.nombre());
        assertEquals(10000, salon.capacidad());
    }

    @Test
    void deberiaRegistrarSalonConNombreDuplicadoDiferenteMayuscula() {
        InMemorySalonRepositoryStub repository = new InMemorySalonRepositoryStub();
        SalonApplicationService service = new SalonApplicationService(
                repository,
                new ReservaSalonRepositoryStub(Set.of())
        );
        service.ejecutar(new RegistrarSalonCommand("Salon Republica", 120, "Principal"));

        assertThrows(
                DomainException.class,
                () -> service.ejecutar(new RegistrarSalonCommand("SALON REPUBLICA", 80, "Auxiliar"))
        );
    }

    @Test
    void deberiaDevolveSalonViewConTodosLosAtributosAlRegistrar() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        SalonView salon = service.ejecutar(new RegistrarSalonCommand("Salon Test", 100, "Descripcion Test"));

        assertEquals("Salon Test", salon.nombre());
        assertEquals(100, salon.capacidad());
        assertEquals("Descripcion Test", salon.descripcion());
        assertEquals(true, salon.activo());
    }

    // --- Tests para obtenerPorId ---
    
    @Test
    void deberiaObtenerSalonPorIdExistente() {
        Salon salonGuardado = Salon.nuevo("Salon Test", 100, "Test");
        InMemorySalonRepositoryStub repository = new InMemorySalonRepositoryStub();
        repository.guardar(salonGuardado);

        SalonApplicationService service = new SalonApplicationService(
                repository,
                new ReservaSalonRepositoryStub(Set.of())
        );

        SalonView salon = service.obtenerPorId(salonGuardado.getId());

        assertEquals(salonGuardado.getId(), salon.id());
        assertEquals("Salon Test", salon.nombre());
        assertEquals(100, salon.capacidad());
    }

    @Test
    void noDeberiaEncontrarSalonPorIdNoExistente() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        assertThrows(
                DomainException.class,
                () -> service.obtenerPorId(UUID.randomUUID())
        );
    }

    // --- Tests para listar ---
    
    @Test
    void deberiaListarVacioSiNoHaySalones() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        List<SalonView> salones = service.listar();

        assertEquals(0, salones.size());
    }

    @Test
    void deberiaListarTodosSalones() {
        Salon salon1 = Salon.nuevo("Salon Uno", 50, "Pequeño");
        Salon salon2 = Salon.nuevo("Salon Dos", 100, "Mediano");
        Salon salon3 = Salon.nuevo("Salon Tres", 200, "Grande");

        InMemorySalonRepositoryStub repository = new InMemorySalonRepositoryStub();
        repository.guardar(salon1);
        repository.guardar(salon2);
        repository.guardar(salon3);

        SalonApplicationService service = new SalonApplicationService(
                repository,
                new ReservaSalonRepositoryStub(Set.of())
        );

        List<SalonView> salones = service.listar();

        assertEquals(3, salones.size());
        assertEquals(salon1.getId(), salones.get(0).id());
        assertEquals(salon2.getId(), salones.get(1).id());
        assertEquals(salon3.getId(), salones.get(2).id());
    }

    // --- Validaciones para ConsultarDisponibilidadSalonesQuery ---
    
    @Test
    void noDeberiaConsultarDisponibilidadConFechaHoraInicio() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        assertThrows(
                DomainException.class,
                () -> service.consultarDisponibilidad(
                        new ConsultarDisponibilidadSalonesQuery(
                                null,
                                LocalDateTime.of(2026, 5, 10, 22, 0),
                                null
                        )
                )
        );
    }

    @Test
    void noDeberiaConsultarDisponibilidadConFechaHoraFin() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        assertThrows(
                DomainException.class,
                () -> service.consultarDisponibilidad(
                        new ConsultarDisponibilidadSalonesQuery(
                                LocalDateTime.of(2026, 5, 10, 18, 0),
                                null,
                                null
                        )
                )
        );
    }

    @Test
    void noDeberiaConsultarDisponibilidadConFechaHoraFinNoPosteriora() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        LocalDateTime fecha = LocalDateTime.of(2026, 5, 10, 18, 0);

        assertThrows(
                DomainException.class,
                () -> service.consultarDisponibilidad(
                        new ConsultarDisponibilidadSalonesQuery(fecha, fecha, null)
                )
        );
    }

    @Test
    void noDeberiaConsultarDisponibilidadConFechaHoraFinAnterior() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        assertThrows(
                DomainException.class,
                () -> service.consultarDisponibilidad(
                        new ConsultarDisponibilidadSalonesQuery(
                                LocalDateTime.of(2026, 5, 10, 22, 0),
                                LocalDateTime.of(2026, 5, 10, 18, 0),
                                null
                        )
                )
        );
    }

    @Test
    void noDeberiaConsultarDisponibilidadConCapacidadMinimaCero() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        assertThrows(
                DomainException.class,
                () -> service.consultarDisponibilidad(
                        new ConsultarDisponibilidadSalonesQuery(
                                LocalDateTime.of(2026, 5, 10, 18, 0),
                                LocalDateTime.of(2026, 5, 10, 22, 0),
                                0
                        )
                )
        );
    }

    @Test
    void noDeberiaConsultarDisponibilidadConCapacidadMinimaNegatva() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        assertThrows(
                DomainException.class,
                () -> service.consultarDisponibilidad(
                        new ConsultarDisponibilidadSalonesQuery(
                                LocalDateTime.of(2026, 5, 10, 18, 0),
                                LocalDateTime.of(2026, 5, 10, 22, 0),
                                -50
                        )
                )
        );
    }

    @Test
    void deberiaConsultarDisponibilidadSinSalones() {
        SalonApplicationService service = new SalonApplicationService(
                new InMemorySalonRepositoryStub(),
                new ReservaSalonRepositoryStub(Set.of())
        );

        List<SalonView> disponibles = service.consultarDisponibilidad(
                new ConsultarDisponibilidadSalonesQuery(
                        LocalDateTime.of(2026, 5, 10, 18, 0),
                        LocalDateTime.of(2026, 5, 10, 22, 0),
                        null
                )
        );

        assertEquals(0, disponibles.size());
    }

    @Test
    void deberiaConsultarDisponibilidadConTodosSalonesOcupados() {
        Salon salon1 = Salon.nuevo("Salon Uno", 80, "Principal");
        Salon salon2 = Salon.nuevo("Salon Dos", 60, "Secundario");

        InMemorySalonRepositoryStub repository = new InMemorySalonRepositoryStub();
        repository.guardar(salon1);
        repository.guardar(salon2);

        SalonApplicationService service = new SalonApplicationService(
                repository,
                new ReservaSalonRepositoryStub(Set.of(salon1.getId(), salon2.getId()))
        );

        List<SalonView> disponibles = service.consultarDisponibilidad(
                new ConsultarDisponibilidadSalonesQuery(
                        LocalDateTime.of(2026, 5, 10, 18, 0),
                        LocalDateTime.of(2026, 5, 10, 22, 0),
                        null
                )
        );

        assertEquals(0, disponibles.size());
    }

    @Test
    void deberiaFiltrarSalonesInactivos() {
        Salon salonActivo = Salon.nuevo("Salon Activo", 100, "Principal");
        Salon salonInactivo = Salon.reconstruir(
                UUID.randomUUID(),
                "Salon Inactivo",
                100,
                "Secundario",
                false
        );

        InMemorySalonRepositoryStub repository = new InMemorySalonRepositoryStub();
        repository.guardar(salonActivo);
        repository.guardar(salonInactivo);

        SalonApplicationService service = new SalonApplicationService(
                repository,
                new ReservaSalonRepositoryStub(Set.of())
        );

        List<SalonView> disponibles = service.consultarDisponibilidad(
                new ConsultarDisponibilidadSalonesQuery(
                        LocalDateTime.of(2026, 5, 10, 18, 0),
                        LocalDateTime.of(2026, 5, 10, 22, 0),
                        null
                )
        );

        assertEquals(1, disponibles.size());
        assertEquals(salonActivo.getId(), disponibles.get(0).id());
    }

    @Test
    void deberiaFiltrarCapacidadExactaEnDisponibilidad() {
        Salon salonExacto = Salon.nuevo("Salon Exacto", 80, "Principal");
        Salon salonMayor = Salon.nuevo("Salon Mayor", 100, "Secundario");

        InMemorySalonRepositoryStub repository = new InMemorySalonRepositoryStub();
        repository.guardar(salonExacto);
        repository.guardar(salonMayor);

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

        assertEquals(2, disponibles.size());
    }

    @Test
    void deberiaConsultarDisponibilidadConMultiplosFiltros() {
        Salon salonLibreSuficiente = Salon.nuevo("Salon A", 100, "Principal");
        Salon salonLibreInsuficiente = Salon.nuevo("Salon B", 50, "Auxiliar");
        Salon salonOcupado = Salon.nuevo("Salon C", 120, "Grande");

        InMemorySalonRepositoryStub repository = new InMemorySalonRepositoryStub();
        repository.guardar(salonLibreSuficiente);
        repository.guardar(salonLibreInsuficiente);
        repository.guardar(salonOcupado);

        SalonApplicationService service = new SalonApplicationService(
                repository,
                new ReservaSalonRepositoryStub(Set.of(salonOcupado.getId()))
        );

        List<SalonView> disponibles = service.consultarDisponibilidad(
                new ConsultarDisponibilidadSalonesQuery(
                        LocalDateTime.of(2026, 5, 10, 18, 0),
                        LocalDateTime.of(2026, 5, 10, 22, 0),
                        80
                )
        );

        assertEquals(1, disponibles.size());
        assertEquals(salonLibreSuficiente.getId(), disponibles.get(0).id());
    }

    @Test
    void deberiaUsarCapacidadMinimaNullParaOmitirFiltro() {
        Salon salonPequeno = Salon.nuevo("Salon Pequeno", 30, "Auxiliar");
        Salon salonGrande = Salon.nuevo("Salon Grande", 150, "Principal");

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
                        null
                )
        );

        assertEquals(2, disponibles.size());
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
        public Optional<ReservaSalon> buscarPorId(UUID id) {
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
