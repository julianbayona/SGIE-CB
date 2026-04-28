package com.ejemplo.monolitomodular.eventos.aplicacion.servicio;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoComida;
import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoEvento;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoComidaRepository;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoEventoRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CrearEventoCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CrearReservaSalonCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.ModificarReservaSalonCommand;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.HistorialEstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.ReservaSalon;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.HistorialEstadoEventoRepository;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.ReservaSalonRepository;
import com.ejemplo.monolitomodular.salones.dominio.modelo.Salon;
import com.ejemplo.monolitomodular.salones.dominio.puerto.salida.SalonRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
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

class EventoApplicationServiceTest {

    @Test
    void deberiaCrearEventoSinReservasIniciales() {
        Cliente cliente = Cliente.nuevo("123", "Ana Perez", "3001112233", "ana@correo.com", TipoCliente.SOCIO, null);
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        UUID tipoEventoId = UUID.randomUUID();
        UUID tipoComidaId = UUID.randomUUID();

        EventoApplicationService service = new EventoApplicationService(
                new ClienteRepositoryStub(List.of(cliente)),
                new TipoEventoRepositoryStub(Set.of(tipoEventoId)),
                new TipoComidaRepositoryStub(Set.of(tipoComidaId)),
                new UsuarioRepositoryStub(List.of(usuario)),
                new SalonRepositoryStub(List.of()),
                new EventoRepositoryStub(),
                new ReservaSalonRepositoryStub(),
                new HistorialRepositoryStub()
        );

        EventoView evento = service.ejecutar(new CrearEventoCommand(
                cliente.getId(),
                tipoEventoId,
                tipoComidaId,
                usuario.getId(),
                LocalDateTime.of(2026, 5, 10, 22, 0),
                LocalDateTime.of(2026, 5, 11, 2, 0)
        ));

        assertEquals(EstadoEvento.PENDIENTE, evento.estado());
        assertEquals(0, evento.reservas().size());
        assertEquals(LocalDateTime.of(2026, 5, 10, 22, 0), evento.fechaHoraInicio());
        assertEquals(LocalDateTime.of(2026, 5, 11, 2, 0), evento.fechaHoraFin());
    }

    @Test
    void deberiaPermitirMultiplesReservasIndependientesEnUnMismoEvento() {
        Cliente cliente = Cliente.nuevo("123", "Ana Perez", "3001112233", "ana@correo.com", TipoCliente.SOCIO, null);
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        Salon salonManana = Salon.nuevo("Salon A", 120, "Primer piso");
        Salon salonTarde = Salon.nuevo("Salon B", 90, "Segundo piso");
        UUID tipoEventoId = UUID.randomUUID();
        UUID tipoComidaId = UUID.randomUUID();

        EventoRepositoryStub eventoRepository = new EventoRepositoryStub();
        ReservaSalonRepositoryStub reservaRepository = new ReservaSalonRepositoryStub();
        EventoApplicationService service = new EventoApplicationService(
                new ClienteRepositoryStub(List.of(cliente)),
                new TipoEventoRepositoryStub(Set.of(tipoEventoId)),
                new TipoComidaRepositoryStub(Set.of(tipoComidaId)),
                new UsuarioRepositoryStub(List.of(usuario)),
                new SalonRepositoryStub(List.of(salonManana, salonTarde)),
                eventoRepository,
                reservaRepository,
                new HistorialRepositoryStub()
        );

        EventoView evento = service.ejecutar(new CrearEventoCommand(
                cliente.getId(),
                tipoEventoId,
                tipoComidaId,
                usuario.getId(),
                LocalDateTime.of(2026, 8, 15, 8, 0),
                LocalDateTime.of(2026, 8, 15, 14, 0)
        ));

        service.ejecutar(evento.id(), new CrearReservaSalonCommand(
                usuario.getId(),
                salonManana.getId(),
                60,
                LocalDateTime.of(2026, 8, 15, 8, 0),
                LocalDateTime.of(2026, 8, 15, 12, 0)
        ));

        EventoView actualizado = service.ejecutar(evento.id(), new CrearReservaSalonCommand(
                usuario.getId(),
                salonTarde.getId(),
                60,
                LocalDateTime.of(2026, 8, 15, 12, 0),
                LocalDateTime.of(2026, 8, 15, 14, 0)
        ));

        assertEquals(2, actualizado.reservas().size());
        assertEquals(Set.of(salonManana.getId(), salonTarde.getId()),
                actualizado.reservas().stream().map(reserva -> reserva.salonId()).collect(java.util.stream.Collectors.toSet()));
    }

    @Test
    void noDeberiaPermitirReservaConSolapamientoConfirmado() {
        Cliente cliente = Cliente.nuevo("123", "Ana Perez", "3001112233", "ana@correo.com", TipoCliente.SOCIO, null);
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        Salon salon = Salon.nuevo("Salon A", 120, "Primer piso");
        UUID tipoEventoId = UUID.randomUUID();
        UUID tipoComidaId = UUID.randomUUID();

        EventoRepositoryStub eventoRepository = new EventoRepositoryStub();
        ReservaSalonRepositoryStub reservaRepository = new ReservaSalonRepositoryStub();
        UUID eventoConfirmadoId = UUID.randomUUID();
        reservaRepository.marcarEventoConfirmado(eventoConfirmadoId);
        reservaRepository.guardarTodas(List.of(
                ReservaSalon.reconstruir(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        eventoConfirmadoId,
                        salon.getId(),
                        60,
                        LocalDateTime.of(2026, 5, 10, 18, 0),
                        LocalDateTime.of(2026, 5, 10, 22, 0),
                        1,
                        true,
                        usuario.getId()
                )
        ));

        EventoApplicationService service = new EventoApplicationService(
                new ClienteRepositoryStub(List.of(cliente)),
                new TipoEventoRepositoryStub(Set.of(tipoEventoId)),
                new TipoComidaRepositoryStub(Set.of(tipoComidaId)),
                new UsuarioRepositoryStub(List.of(usuario)),
                new SalonRepositoryStub(List.of(salon)),
                eventoRepository,
                reservaRepository,
                new HistorialRepositoryStub()
        );

        EventoView evento = service.ejecutar(new CrearEventoCommand(
                cliente.getId(),
                tipoEventoId,
                tipoComidaId,
                usuario.getId(),
                LocalDateTime.of(2026, 5, 10, 18, 0),
                LocalDateTime.of(2026, 5, 10, 23, 0)
        ));

        assertThrows(
                DomainException.class,
                () -> service.ejecutar(evento.id(), new CrearReservaSalonCommand(
                        usuario.getId(),
                        salon.getId(),
                        70,
                        LocalDateTime.of(2026, 5, 10, 19, 0),
                        LocalDateTime.of(2026, 5, 10, 21, 0)
                ))
        );
    }

    @Test
    void deberiaCrearNuevaVersionDeReservaAlModificarla() {
        Cliente cliente = Cliente.nuevo("123", "Ana Perez", "3001112233", "ana@correo.com", TipoCliente.SOCIO, null);
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        Salon salonActual = Salon.nuevo("Salon Republica", 120, "Principal");
        Salon salonNuevo = Salon.nuevo("Salon Colonial", 90, "Segundo piso");
        UUID tipoEventoId = UUID.randomUUID();
        UUID tipoComidaId = UUID.randomUUID();

        EventoRepositoryStub eventoRepository = new EventoRepositoryStub();
        ReservaSalonRepositoryStub reservaRepository = new ReservaSalonRepositoryStub();

        EventoApplicationService service = new EventoApplicationService(
                new ClienteRepositoryStub(List.of(cliente)),
                new TipoEventoRepositoryStub(Set.of(tipoEventoId)),
                new TipoComidaRepositoryStub(Set.of(tipoComidaId)),
                new UsuarioRepositoryStub(List.of(usuario)),
                new SalonRepositoryStub(List.of(salonActual, salonNuevo)),
                eventoRepository,
                reservaRepository,
                new HistorialRepositoryStub()
        );

        EventoView evento = service.ejecutar(new CrearEventoCommand(
                cliente.getId(),
                tipoEventoId,
                tipoComidaId,
                usuario.getId(),
                LocalDateTime.of(2026, 7, 10, 20, 0),
                LocalDateTime.of(2026, 7, 11, 3, 0)
        ));

        EventoView conReserva = service.ejecutar(evento.id(), new CrearReservaSalonCommand(
                usuario.getId(),
                salonActual.getId(),
                80,
                LocalDateTime.of(2026, 7, 10, 20, 0),
                LocalDateTime.of(2026, 7, 11, 1, 0)
        ));

        UUID reservaRaizId = conReserva.reservas().get(0).reservaRaizId();

        EventoView modificado = service.ejecutar(
                reservaRaizId,
                new ModificarReservaSalonCommand(
                        usuario.getId(),
                        salonNuevo.getId(),
                        95,
                        LocalDateTime.of(2026, 7, 10, 21, 0),
                        LocalDateTime.of(2026, 7, 11, 2, 30)
                )
        );

        assertEquals(1, modificado.reservas().size());
        assertEquals(2, modificado.reservas().get(0).version());
        assertEquals(salonNuevo.getId(), modificado.reservas().get(0).salonId());
        assertEquals(2, reservaRepository.totalVersiones());
    }

    private static class ClienteRepositoryStub implements ClienteRepository {

        private final List<Cliente> clientes;

        private ClienteRepositoryStub(List<Cliente> clientes) {
            this.clientes = clientes;
        }

        @Override
        public Cliente guardar(Cliente cliente) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Cliente> buscarPorId(UUID id) {
            return clientes.stream().filter(cliente -> cliente.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<Cliente> buscarPorCedula(String cedula) {
            return Optional.empty();
        }

        @Override
        public List<Cliente> listar() {
            return clientes;
        }

        @Override
        public List<Cliente> buscarPorFiltro(String filtro) {
            return clientes;
        }
    }

    private static class SalonRepositoryStub implements SalonRepository {

        private final List<Salon> salones;

        private SalonRepositoryStub(List<Salon> salones) {
            this.salones = salones;
        }

        @Override
        public Salon guardar(Salon salon) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Salon> buscarPorId(UUID id) {
            return salones.stream().filter(salon -> salon.getId().equals(id)).findFirst();
        }

        @Override
        public List<Salon> listar() {
            return salones;
        }

        @Override
        public List<Salon> buscarTodosPorIds(Collection<UUID> ids) {
            return salones.stream().filter(salon -> ids.contains(salon.getId())).toList();
        }

        @Override
        public boolean existePorNombre(String nombre) {
            return false;
        }
    }

    private static class TipoEventoRepositoryStub implements TipoEventoRepository {

        private final Set<UUID> activos;

        private TipoEventoRepositoryStub(Set<UUID> activos) {
            this.activos = activos;
        }

        @Override
        public TipoEvento guardar(TipoEvento tipoEvento) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<TipoEvento> buscarPorId(UUID id) {
            return Optional.empty();
        }

        @Override
        public List<TipoEvento> listar() {
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

    private static class TipoComidaRepositoryStub implements TipoComidaRepository {

        private final Set<UUID> activos;

        private TipoComidaRepositoryStub(Set<UUID> activos) {
            this.activos = activos;
        }

        @Override
        public TipoComida guardar(TipoComida tipoComida) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<TipoComida> buscarPorId(UUID id) {
            return Optional.empty();
        }

        @Override
        public List<TipoComida> listar() {
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

    private static class EventoRepositoryStub implements EventoRepository {

        private final List<Evento> eventos = new ArrayList<>();

        @Override
        public Evento guardar(Evento evento) {
            eventos.removeIf(actual -> actual.getId().equals(evento.getId()));
            eventos.add(evento);
            return evento;
        }

        @Override
        public Optional<Evento> buscarPorId(UUID id) {
            return eventos.stream().filter(evento -> evento.getId().equals(id)).findFirst();
        }

        @Override
        public List<Evento> listar() {
            return List.copyOf(eventos);
        }
    }

    private static class ReservaSalonRepositoryStub implements ReservaSalonRepository {

        private final List<ReservaSalon> reservas = new ArrayList<>();
        private final Set<UUID> eventosConfirmados = new java.util.HashSet<>();

        void marcarEventoConfirmado(UUID eventoId) {
            eventosConfirmados.add(eventoId);
        }

        @Override
        public List<ReservaSalon> guardarTodas(List<ReservaSalon> reservas) {
            this.reservas.addAll(reservas);
            return reservas;
        }

        @Override
        public ReservaSalon guardar(ReservaSalon reserva) {
            reservas.removeIf(actual -> actual.getId().equals(reserva.getId()));
            reservas.add(reserva);
            return reserva;
        }

        @Override
        public boolean existeConflicto(UUID salonId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
            return existeConflicto(salonId, fechaHoraInicio, fechaHoraFin, null);
        }

        @Override
        public boolean existeConflicto(UUID salonId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, UUID reservaRaizIdExcluida) {
            return reservas.stream()
                    .filter(ReservaSalon::isVigente)
                    .filter(reserva -> reserva.getSalonId().equals(salonId))
                    .filter(reserva -> reservaRaizIdExcluida == null || !reserva.getReservaRaizId().equals(reservaRaizIdExcluida))
                    .filter(reserva -> eventosConfirmados.contains(reserva.getEventoId()))
                    .anyMatch(reserva -> reserva.getFechaHoraInicio().isBefore(fechaHoraFin)
                            && reserva.getFechaHoraFin().isAfter(fechaHoraInicio));
        }

        @Override
        public List<ReservaSalon> listarPorEvento(UUID eventoId) {
            return reservas.stream()
                    .filter(ReservaSalon::isVigente)
                    .filter(reserva -> reserva.getEventoId().equals(eventoId))
                    .toList();
        }

        @Override
        public Set<UUID> buscarSalonesOcupados(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
            return reservas.stream()
                    .filter(ReservaSalon::isVigente)
                    .filter(reserva -> eventosConfirmados.contains(reserva.getEventoId()))
                    .filter(reserva -> reserva.getFechaHoraInicio().isBefore(fechaHoraFin)
                            && reserva.getFechaHoraFin().isAfter(fechaHoraInicio))
                    .map(ReservaSalon::getSalonId)
                    .collect(java.util.stream.Collectors.toSet());
        }

        @Override
        public Optional<ReservaSalon> buscarVigentePorEventoYSalon(UUID eventoId, UUID salonId) {
            return reservas.stream()
                    .filter(ReservaSalon::isVigente)
                    .filter(reserva -> reserva.getEventoId().equals(eventoId))
                    .filter(reserva -> reserva.getSalonId().equals(salonId))
                    .findFirst();
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

    private static class HistorialRepositoryStub implements HistorialEstadoEventoRepository {

        @Override
        public HistorialEstadoEvento guardar(HistorialEstadoEvento historialEstadoEvento) {
            return historialEstadoEvento;
        }
    }

    private static class UsuarioRepositoryStub implements UsuarioRepository {

        private final List<Usuario> usuarios;

        private UsuarioRepositoryStub(List<Usuario> usuarios) {
            this.usuarios = usuarios;
        }

        @Override
        public Usuario guardar(Usuario usuario) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Usuario> buscarPorId(UUID id) {
            return usuarios.stream().filter(usuario -> usuario.getId().equals(id)).findFirst();
        }
    }
}
