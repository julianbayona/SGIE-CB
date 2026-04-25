package com.ejemplo.monolitomodular.eventos.aplicacion.servicio;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.CrearEventoCommand;
import com.ejemplo.monolitomodular.eventos.aplicacion.dto.EventoView;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventoApplicationServiceTest {

    @Test
    void deberiaCrearEventoConReservaEHistorial() {
        Cliente cliente = Cliente.nuevo("123", "Ana", "3001112233", "ana@correo.com", TipoCliente.SOCIO);
        Salon salon = Salon.nuevo("Salon Republica", 120, "Principal");
        Usuario usuario = Usuario.nuevo("Admin", "admin@club.com", "$2a$hash", RolUsuario.ADMINISTRADOR);

        EventoApplicationService service = new EventoApplicationService(
                new ClienteRepositoryStub(List.of(cliente)),
                new SalonRepositoryStub(List.of(salon)),
                new EventoRepositoryStub(),
                new ReservaSalonRepositoryStub(),
                new HistorialRepositoryStub(),
                new UsuarioRepositoryStub(List.of(usuario))
        );

        EventoView evento = service.ejecutar(new CrearEventoCommand(
                cliente.getId(),
                "Boda",
                "Cena",
                LocalDate.of(2026, 5, 10),
                LocalTime.of(18, 0),
                4,
                80,
                List.of(salon.getId()),
                "Evento principal",
                usuario.getId()
        ));

        assertEquals(EstadoEvento.PENDIENTE, evento.estado());
        assertEquals(1, evento.salonIds().size());
    }

    @Test
    void noDeberiaPermitirReservaConSolapamiento() {
        Cliente cliente = Cliente.nuevo("123", "Ana", "3001112233", "ana@correo.com", TipoCliente.SOCIO);
        Salon salon = Salon.nuevo("Salon Republica", 120, "Principal");

        ReservaSalonRepositoryStub reservaRepository = new ReservaSalonRepositoryStub();
        reservaRepository.guardarTodas(List.of(
                ReservaSalon.nueva(
                        UUID.randomUUID(),
                        salon.getId(),
                        LocalDateTime.of(2026, 5, 10, 18, 0),
                        LocalDateTime.of(2026, 5, 10, 22, 0)
                )
        ));

        EventoApplicationService service = new EventoApplicationService(
                new ClienteRepositoryStub(List.of(cliente)),
                new SalonRepositoryStub(List.of(salon)),
                new EventoRepositoryStub(),
                reservaRepository,
                new HistorialRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        assertThrows(
                DomainException.class,
                () -> service.ejecutar(new CrearEventoCommand(
                        cliente.getId(),
                        "Cumpleanos",
                        "Cena",
                        LocalDate.of(2026, 5, 10),
                        LocalTime.of(19, 0),
                        3,
                        60,
                        List.of(salon.getId()),
                        "",
                        null
                ))
        );
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

    private static class EventoRepositoryStub implements EventoRepository {

        private final List<Evento> eventos = new ArrayList<>();

        @Override
        public Evento guardar(Evento evento) {
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

        @Override
        public List<ReservaSalon> guardarTodas(List<ReservaSalon> reservas) {
            this.reservas.addAll(reservas);
            return reservas;
        }

        @Override
        public boolean existeConflicto(UUID salonId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
            return reservas.stream()
                    .filter(reserva -> reserva.getSalonId().equals(salonId))
                    .anyMatch(reserva -> reserva.getFechaInicio().isBefore(fechaFin)
                            && reserva.getFechaFin().isAfter(fechaInicio));
        }

        @Override
        public List<ReservaSalon> listarPorEvento(UUID eventoId) {
            return reservas.stream().filter(reserva -> reserva.getEventoId().equals(eventoId)).toList();
        }
    }

    private static class HistorialRepositoryStub implements HistorialEstadoEventoRepository {

        private final List<HistorialEstadoEvento> historial = new ArrayList<>();

        @Override
        public HistorialEstadoEvento guardar(HistorialEstadoEvento historialEstadoEvento) {
            historial.add(historialEstadoEvento);
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

        @Override
        public Optional<Usuario> buscarPorEmail(String email) {
            return usuarios.stream().filter(usuario -> usuario.getEmail().equalsIgnoreCase(email)).findFirst();
        }
    }
}
