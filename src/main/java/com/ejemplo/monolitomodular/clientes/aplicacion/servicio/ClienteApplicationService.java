package com.ejemplo.monolitomodular.clientes.aplicacion.servicio;

import com.ejemplo.monolitomodular.clientes.aplicacion.dto.ClienteView;
import com.ejemplo.monolitomodular.clientes.aplicacion.dto.RegistrarClienteCommand;
import com.ejemplo.monolitomodular.clientes.aplicacion.puerto.entrada.ConsultarClienteUseCase;
import com.ejemplo.monolitomodular.clientes.aplicacion.puerto.entrada.RegistrarClienteUseCase;
import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import com.ejemplo.monolitomodular.shared.dominio.excepcion.DomainException;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClienteApplicationService implements RegistrarClienteUseCase, ConsultarClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public ClienteApplicationService(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public ClienteView ejecutar(RegistrarClienteCommand command) {
        if (command.creadoPor() != null) {
            usuarioRepository.buscarPorId(command.creadoPor())
                    .orElseThrow(() -> new DomainException("El usuario creador del cliente no existe"));
        }

        Cliente cliente = Cliente.nuevo(
                command.cedula(),
                command.nombreCompleto(),
                command.telefono(),
                command.correo(),
                command.tipoCliente(),
                command.creadoPor()
        );

        clienteRepository.buscarPorCedula(cliente.getCedula())
                .ifPresent(clienteExistente -> {
                    throw new DomainException("Ya existe un cliente con la cedula indicada");
                });

        return toView(clienteRepository.guardar(cliente));
    }

    @Override
    public ClienteView obtenerPorId(UUID id) {
        return clienteRepository.buscarPorId(id)
                .map(this::toView)
                .orElseThrow(() -> new DomainException("Cliente no encontrado"));
    }

    @Override
    public List<ClienteView> listar() {
        return clienteRepository.listar().stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public List<ClienteView> buscar(String filtro) {
        return clienteRepository.buscarPorFiltro(filtro).stream()
                .map(this::toView)
                .toList();
    }

    private ClienteView toView(Cliente cliente) {
        return new ClienteView(
                cliente.getId(),
                cliente.getCedula(),
                cliente.getNombreCompleto(),
                cliente.getTelefono(),
                cliente.getCorreo(),
                cliente.getTipoCliente(),
                cliente.isActivo(),
                cliente.getCreadoPor()
        );
    }
}
