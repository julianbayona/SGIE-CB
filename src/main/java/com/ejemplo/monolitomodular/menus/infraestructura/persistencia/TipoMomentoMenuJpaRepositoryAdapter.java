package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import com.ejemplo.monolitomodular.menus.dominio.modelo.TipoMomentoMenu;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.TipoMomentoMenuRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TipoMomentoMenuJpaRepositoryAdapter implements TipoMomentoMenuRepository {

    private final SpringDataTipoMomentoMenuJpaRepository repository;

    public TipoMomentoMenuJpaRepositoryAdapter(SpringDataTipoMomentoMenuJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public TipoMomentoMenu guardar(TipoMomentoMenu tipoMomentoMenu) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = repository.findById(tipoMomentoMenu.getId())
                .map(TipoMomentoMenuJpaEntity::getCreatedAt)
                .orElse(now);
        TipoMomentoMenuJpaEntity entity = new TipoMomentoMenuJpaEntity(
                tipoMomentoMenu.getId(),
                tipoMomentoMenu.getNombre(),
                tipoMomentoMenu.isActivo(),
                createdAt,
                now
        );
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<TipoMomentoMenu> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<TipoMomentoMenu> listar() {
        return repository.findAllByOrderByNombreAsc().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existeActivoPorId(UUID id) {
        return repository.existsByIdAndActivoTrue(id);
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return repository.existsByNombreIgnoreCase(nombre);
    }

    private TipoMomentoMenu toDomain(TipoMomentoMenuJpaEntity entity) {
        return TipoMomentoMenu.reconstruir(entity.getId(), entity.getNombre(), entity.isActivo());
    }
}
