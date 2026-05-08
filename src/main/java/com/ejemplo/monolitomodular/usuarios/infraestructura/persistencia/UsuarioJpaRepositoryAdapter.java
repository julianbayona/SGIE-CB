package com.ejemplo.monolitomodular.usuarios.infraestructura.persistencia;

import com.ejemplo.monolitomodular.usuarios.dominio.modelo.Usuario;
import com.ejemplo.monolitomodular.usuarios.dominio.modelo.RolUsuario;
import com.ejemplo.monolitomodular.usuarios.dominio.puerto.salida.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UsuarioJpaRepositoryAdapter implements UsuarioRepository {

    private final SpringDataUsuarioJpaRepository repository;

    public UsuarioJpaRepositoryAdapter(SpringDataUsuarioJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = repository.findById(usuario.getId())
                .map(UsuarioJpaEntity::getCreatedAt)
                .orElse(now);
        UsuarioJpaEntity entity = new UsuarioJpaEntity(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getContrasenaHash(),
                usuario.getRol(),
                usuario.isActivo(),
                createdAt,
                now
        );
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Usuario> listar() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public long contarAdministradoresActivos() {
        return repository.countByRolAndActivoTrue(RolUsuario.ADMINISTRADOR);
    }

    @Override
    public Optional<Usuario> buscarPorNombre(String nombre) {
        return repository.findFirstByNombreIgnoreCase(nombre.trim()).map(this::toDomain);
    }

    private Usuario toDomain(UsuarioJpaEntity entity) {
        return Usuario.reconstruir(
                entity.getId(),
                entity.getNombre(),
                entity.getContrasenaHash(),
                entity.getRol(),
                entity.isActivo()
        );
    }
}
