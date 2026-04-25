package com.ejemplo.monolitomodular.clientes.aplicacion.servicio;

import com.ejemplo.monolitomodular.clientes.aplicacion.dto.ClienteView;
import com.ejemplo.monolitomodular.clientes.aplicacion.dto.RegistrarClienteCommand;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.TipoCliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
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
        ClienteApplicationService service = new ClienteApplicationService(new InMemoryClienteRepositoryStub());

        ClienteView cliente = service.ejecutar(
                new RegistrarClienteCommand("123", "Ana", "3001112233", "ana@correo.com", TipoCliente.SOCIO)
        );

        assertNotNull(cliente.id());
        assertEquals("123", cliente.cedula());
        assertEquals("Ana", cliente.nombre());
        assertEquals("3001112233", cliente.telefono());
        assertEquals("ana@correo.com", cliente.correo());
    }

    @Test
    void noDeberiaPermitirCedulasDuplicadas() {
        InMemoryClienteRepositoryStub repository = new InMemoryClienteRepositoryStub();
        ClienteApplicationService service = new ClienteApplicationService(repository);
        service.ejecutar(
                new RegistrarClienteCommand("123", "Ana", "3001112233", "ana@correo.com", TipoCliente.SOCIO)
        );

        assertThrows(
                DomainException.class,
                () -> service.ejecutar(
                        new RegistrarClienteCommand("123", "Otra Ana", "3004445566", "otra@correo.com", TipoCliente.NO_SOCIO)
                )
        );
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
                            || cliente.getNombre().contains(filtro)
                            || cliente.getTelefono().contains(filtro))
                    .toList();
        }
    }
}
