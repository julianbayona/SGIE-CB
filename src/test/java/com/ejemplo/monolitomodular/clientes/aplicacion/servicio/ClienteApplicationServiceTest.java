package com.ejemplo.monolitomodular.clientes.aplicacion.servicio;

import com.ejemplo.monolitomodular.clientes.aplicacion.dto.ClienteView;
import com.ejemplo.monolitomodular.clientes.aplicacion.dto.RegistrarClienteCommand;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClienteApplicationServiceTest {

    @Test
    void deberiaRegistrarCliente() {
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of(usuario))
        );

        ClienteView cliente = service.ejecutar(
                new RegistrarClienteCommand(
                        "123",
                        "Ana Perez",
                        "3001112233",
                        "ana@correo.com",
                        TipoCliente.SOCIO,
                        usuario.getId()
                )
        );

        assertNotNull(cliente.id());
        assertEquals("123", cliente.cedula());
        assertEquals("Ana Perez", cliente.nombreCompleto());
        assertEquals("3001112233", cliente.telefono());
        assertEquals(usuario.getId(), cliente.creadoPor());
    }

    @Test
    void noDeberiaPermitirCedulasDuplicadas() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );
        service.ejecutar(
                new RegistrarClienteCommand("123", "Ana Perez", "3001112233", "ana@correo.com", TipoCliente.SOCIO, null)
        );

        assertThrows(
                DomainException.class,
                () -> service.ejecutar(
                        new RegistrarClienteCommand("123", "Otra Ana", "3004445566", "otra@correo.com", TipoCliente.NO_SOCIO, null)
                )
        );
    }

    

    @Test
    void deberiaRegistrarClienteConUsuarioCreador() {
        Usuario usuario = Usuario.nuevo("Admin", "$2a$hash", RolUsuario.ADMINISTRADOR);
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of(usuario))
        );

        ClienteView cliente = service.ejecutar(
                new RegistrarClienteCommand(
                        "456",
                        "Juan Gomez",
                        "3104445566",
                        "juan@correo.com",
                        TipoCliente.SOCIO,
                        usuario.getId()
                )
        );

        assertEquals("456", cliente.cedula());
        assertEquals("Juan Gomez", cliente.nombreCompleto());
        assertEquals(usuario.getId(), cliente.creadoPor());
    }

    @Test
    void noDeberiaRegistrarClienteConUsuarioCreadorNoExistente() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        assertThrows(
                DomainException.class,
                () -> service.ejecutar(
                        new RegistrarClienteCommand(
                                "789",
                                "Maria Lopez",
                                "3205556677",
                                "maria@correo.com",
                                TipoCliente.NO_SOCIO,
                                UUID.randomUUID()
                        )
                )
        );
    }

    @Test
    void deberiaRegistrarClienteNoSocio() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        ClienteView cliente = service.ejecutar(
                new RegistrarClienteCommand(
                        "999",
                        "Pedro Rodriguez",
                        "3306667788",
                        "pedro@correo.com",
                        TipoCliente.NO_SOCIO,
                        null
                )
        );

        assertEquals(TipoCliente.NO_SOCIO, cliente.tipoCliente());
    }

    @Test
    void deberiaObtenerClientePorId() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        ClienteView creado = service.ejecutar(
                new RegistrarClienteCommand(
                        "111",
                        "Carlos Sanchez",
                        "3007778899",
                        "carlos@correo.com",
                        TipoCliente.SOCIO,
                        null
                )
        );

        ClienteView obtenido = service.obtenerPorId(creado.id());

        assertEquals("111", obtenido.cedula());
        assertEquals("Carlos Sanchez", obtenido.nombreCompleto());
    }

    @Test
    void noDeberiaObtenerClienteNoExistente() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        assertThrows(DomainException.class, () -> service.obtenerPorId(UUID.randomUUID()));
    }

    @Test
    void deberiaListarClientesVacio() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        assertEquals(0, service.listar().size());
    }

    @Test
    void deberiaListarMultiplesClientes() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        service.ejecutar(new RegistrarClienteCommand("111", "Cliente 1", "3001111111", "c1@correo.com", TipoCliente.SOCIO, null));
        service.ejecutar(new RegistrarClienteCommand("222", "Cliente 2", "3002222222", "c2@correo.com", TipoCliente.NO_SOCIO, null));
        service.ejecutar(new RegistrarClienteCommand("333", "Cliente 3", "3003333333", "c3@correo.com", TipoCliente.SOCIO, null));

        assertEquals(3, service.listar().size());
    }

    @Test
    void deberiaActualizarClienteAlRegistrarOtraVezMismaCedula() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        ClienteView cliente1 = service.ejecutar(
                new RegistrarClienteCommand("444", "Ana", "3004444444", "ana@correo.com", TipoCliente.SOCIO, null)
        );
        assertEquals("Ana", cliente1.nombreCompleto());

        // Intentar registrar con la misma cédula debe fallar
        assertThrows(
                DomainException.class,
                () -> service.ejecutar(
                        new RegistrarClienteCommand("444", "Otro Ana", "3004444445", "otro@correo.com", TipoCliente.NO_SOCIO, null)
                )
        );
    }

    @Test
    void deberiaObtenerTelefonoDelCliente() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        ClienteView creado = service.ejecutar(
                new RegistrarClienteCommand("555", "David Mora", "3105555555", "david@correo.com", TipoCliente.SOCIO, null)
        );

        assertEquals("3105555555", creado.telefono());
    }

    @Test
    void deberiaObtenerCorreoDelCliente() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        ClienteView creado = service.ejecutar(
                new RegistrarClienteCommand("666", "Sofia Ruiz", "3106666666", "sofia@ejemplo.com", TipoCliente.NO_SOCIO, null)
        );

        assertEquals("sofia@ejemplo.com", creado.correo());
    }

    @Test
    void deberiaFiltrarClientesPorCedula() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        service.ejecutar(new RegistrarClienteCommand("777", "Cliente A", "3107777777", "ca@correo.com", TipoCliente.SOCIO, null));
        service.ejecutar(new RegistrarClienteCommand("888", "Cliente B", "3108888888", "cb@correo.com", TipoCliente.SOCIO, null));
        service.ejecutar(new RegistrarClienteCommand("999", "Cliente C", "3109999999", "cc@correo.com", TipoCliente.NO_SOCIO, null));

        List<ClienteView> resultado = service.buscar("777");

        assertEquals(1, resultado.size());
        assertEquals("777", resultado.get(0).cedula());
    }

    @Test
    void deberiaFiltrarClientesPorNombre() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        service.ejecutar(new RegistrarClienteCommand("101", "Alfonso Martinez", "3101010101", "am@correo.com", TipoCliente.SOCIO, null));
        service.ejecutar(new RegistrarClienteCommand("102", "Ana Garcia", "3102020202", "ag@correo.com", TipoCliente.NO_SOCIO, null));

        List<ClienteView> resultado = service.buscar("Ana");

        assertEquals(1, resultado.size());
        assertEquals("Ana Garcia", resultado.get(0).nombreCompleto());
    }

    @Test
    void deberiaFiltrarClientesPorTelefono() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        service.ejecutar(new RegistrarClienteCommand("103", "Laura Hernandez", "3201030101", "lh@correo.com", TipoCliente.SOCIO, null));
        service.ejecutar(new RegistrarClienteCommand("104", "Roberto Diaz", "3201040404", "rd@correo.com", TipoCliente.NO_SOCIO, null));

        List<ClienteView> resultado = service.buscar("3201030101");

        assertEquals(1, resultado.size());
        assertEquals("3201030101", resultado.get(0).telefono());
    }

    @Test
    void deberiaRetornarVacioAlBuscarClienteNoExistente() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        service.ejecutar(new RegistrarClienteCommand("105", "Cliente Test", "3105050505", "test@correo.com", TipoCliente.SOCIO, null));

        List<ClienteView> resultado = service.buscar("noexiste");

        assertEquals(0, resultado.size());
    }

    @Test
    void deberiaObtenerClienteActivoPorDefecto() {
        ClienteApplicationService service = new ClienteApplicationService(
                new InMemoryClienteRepositoryStub(),
                new UsuarioRepositoryStub(List.of())
        );

        ClienteView creado = service.ejecutar(
                new RegistrarClienteCommand("106", "Cliente Activo", "3106060606", "ca@correo.com", TipoCliente.SOCIO, null)
        );

        assertEquals(true, creado.activo());
    }

    private static class InMemoryClienteRepositoryStub implements ClienteRepository {

        private final List<Cliente> clientes = new ArrayList<>();

        @Override
        public Cliente guardar(Cliente cliente) {
            clientes.add(cliente);
            return cliente;
        }

        @Override
        public Optional<Cliente> buscarPorId(UUID id) {
            return clientes.stream().filter(cliente -> cliente.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<Cliente> buscarPorCedula(String cedula) {
            return clientes.stream().filter(cliente -> cliente.getCedula().equalsIgnoreCase(cedula)).findFirst();
        }

        @Override
        public List<Cliente> listar() {
            return List.copyOf(clientes);
        }

        @Override
        public List<Cliente> buscarPorFiltro(String filtro) {
            return clientes.stream()
                    .filter(cliente -> cliente.getCedula().contains(filtro)
                            || cliente.getNombreCompleto().contains(filtro)
                            || cliente.getTelefono().contains(filtro))
                    .toList();
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
