package com.ejemplo.monolitomodular.pruebasplato.aplicacion.servicio;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.EstadoEvento;
import com.ejemplo.monolitomodular.eventos.dominio.modelo.Evento;
import com.ejemplo.monolitomodular.eventos.dominio.puerto.salida.EventoRepository;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.dto.ProgramarPruebaPlatoCommand;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.dto.PruebaPlatoView;
import com.ejemplo.monolitomodular.pruebasplato.aplicacion.evento.PruebaPlatoProgramadaEvent;
import com.ejemplo.monolitomodular.pruebasplato.dominio.modelo.EstadoPruebaPlato;
import com.ejemplo.monolitomodular.pruebasplato.dominio.modelo.PruebaPlato;
import com.ejemplo.monolitomodular.pruebasplato.dominio.puerto.salida.PruebaPlatoRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PruebaPlatoApplicationServiceTest {

    @Test
    void deberiaProgramarPruebaPlatoYPublicarEvento() {
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        Cliente cliente = Cliente.nuevo("123", "Cliente Uno", "573001112233", "cliente@test.com", TipoCliente.NO_SOCIO, usuario.getId());
        Evento evento = evento(cliente.getId(), usuario.getId());
        PruebaPlatoRepositoryStub pruebaRepository = new PruebaPlatoRepositoryStub();
        ApplicationEventPublisherStub eventPublisher = new ApplicationEventPublisherStub();
        PruebaPlatoApplicationService service = new PruebaPlatoApplicationService(
                pruebaRepository,
                new EventoRepositoryStub(evento),
                new ClienteRepositoryStub(cliente),
                new UsuarioRepositoryStub(usuario),
                eventPublisher
        );

        PruebaPlatoView view = service.ejecutar(new ProgramarPruebaPlatoCommand(
                evento.getId(),
                usuario.getId(),
                LocalDateTime.now().plusDays(2)
        ));

        assertEquals(evento.getId(), view.eventoId());
        assertEquals(EstadoPruebaPlato.PROGRAMADA, view.estado());
        assertEquals(view.id(), pruebaRepository.guardada().getId());
        PruebaPlatoProgramadaEvent event = (PruebaPlatoProgramadaEvent) eventPublisher.event();
        assertEquals(view.id(), event.pruebaPlatoId());
        assertEquals(evento.getId(), event.eventoId());
        assertEquals(cliente.getId(), event.clienteId());
        assertEquals(cliente.getNombreCompleto(), event.nombreCliente());
        assertEquals(cliente.getTelefono(), event.telefonoCliente());
    }

    @Test
    void noDeberiaProgramarPruebaPlatoEnFechaPasada() {
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        Cliente cliente = Cliente.nuevo("123", "Cliente Uno", "573001112233", "cliente@test.com", TipoCliente.NO_SOCIO, usuario.getId());
        Evento evento = evento(cliente.getId(), usuario.getId());
        PruebaPlatoApplicationService service = new PruebaPlatoApplicationService(
                new PruebaPlatoRepositoryStub(),
                new EventoRepositoryStub(evento),
                new ClienteRepositoryStub(cliente),
                new UsuarioRepositoryStub(usuario),
                new ApplicationEventPublisherStub()
        );

        assertThrows(DomainException.class, () -> service.ejecutar(new ProgramarPruebaPlatoCommand(
                evento.getId(),
                usuario.getId(),
                LocalDateTime.now().minusDays(1)
        )));
    }

    private static Evento evento(UUID clienteId, UUID usuarioId) {
        return Evento.reconstruir(
                UUID.randomUUID(),
                clienteId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                usuarioId,
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(10).plusHours(4),
                EstadoEvento.PENDIENTE,
                null
        );
    }

    private static class PruebaPlatoRepositoryStub implements PruebaPlatoRepository {

        private PruebaPlato guardada;

        @Override
        public PruebaPlato guardar(PruebaPlato pruebaPlato) {
            this.guardada = pruebaPlato;
            return pruebaPlato;
        }

        PruebaPlato guardada() {
            return guardada;
        }
    }

    private static class EventoRepositoryStub implements EventoRepository {

        private final Evento evento;

        private EventoRepositoryStub(Evento evento) {
            this.evento = evento;
        }

        @Override
        public Evento guardar(Evento evento) {
            throw new UnsupportedOperationException();
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

    private static class ClienteRepositoryStub implements ClienteRepository {

        private final Cliente cliente;

        private ClienteRepositoryStub(Cliente cliente) {
            this.cliente = cliente;
        }

        @Override
        public Cliente guardar(Cliente cliente) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Cliente> buscarPorId(UUID id) {
            return cliente.getId().equals(id) ? Optional.of(cliente) : Optional.empty();
        }

        @Override
        public Optional<Cliente> buscarPorCedula(String cedula) {
            return Optional.empty();
        }

        @Override
        public List<Cliente> listar() {
            return List.of(cliente);
        }

        @Override
        public List<Cliente> buscarPorFiltro(String filtro) {
            return List.of(cliente);
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

    private static class ApplicationEventPublisherStub implements ApplicationEventPublisher {

        private Object event;

        @Override
        public void publishEvent(Object event) {
            this.event = event;
        }

        Object event() {
            return event;
        }
    }
}
