package com.ejemplo.monolitomodular.clientes.infraestructura.persistencia;

import com.ejemplo.monolitomodular.clientes.dominio.modelo.Cliente;
import com.ejemplo.monolitomodular.clientes.dominio.puerto.salida.ClienteRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClienteJpaRepositoryAdapter implements ClienteRepository {

    private final SpringDataClienteJpaRepository repository;

    public ClienteJpaRepositoryAdapter(SpringDataClienteJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        LocalDateTime now = LocalDateTime.now();
        ClienteJpaEntity entity = new ClienteJpaEntity(
                cliente.getId(),
                cliente.getCedula(),
                cliente.getNombreCompleto(),
                cliente.getTelefono(),
                cliente.getCorreo(),
                cliente.getTipoCliente(),
                cliente.isActivo(),
                cliente.getCreadoPor(),
                now,
                now
        );
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorCedula(String cedula) {
        return repository.findByCedulaIgnoreCase(cedula).map(this::toDomain);
    }

    @Override
    public List<Cliente> listar() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .sorted((a, b) -> a.getNombreCompleto().compareToIgnoreCase(b.getNombreCompleto()))
                .toList();
    }

    @Override
    public List<Cliente> buscarPorFiltro(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return listar();
        }
        return repository.buscarPorFiltro(filtro.trim()).stream()
                .map(this::toDomain)
                .toList();
    }

    private Cliente toDomain(ClienteJpaEntity entity) {
        return Cliente.reconstruir(
                entity.getId(),
                entity.getCedula(),
                entity.getNombreCompleto(),
                entity.getTelefono(),
                entity.getCorreo(),
                entity.getTipoCliente(),
                entity.isActivo(),
                entity.getCreadoPor()
        );
    }
}
