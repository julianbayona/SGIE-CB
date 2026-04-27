package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoComida;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoComidaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TipoComidaJpaRepositoryAdapter implements TipoComidaRepository {

    private final SpringDataTipoComidaJpaRepository repository;

    public TipoComidaJpaRepositoryAdapter(SpringDataTipoComidaJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public TipoComida guardar(TipoComida tipoComida) {
        LocalDateTime now = LocalDateTime.now();
        TipoComidaJpaEntity entity = new TipoComidaJpaEntity(
                tipoComida.getId(),
                tipoComida.getNombre(),
                tipoComida.getDescripcion(),
                tipoComida.isActivo(),
                now,
                now
        );
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<TipoComida> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<TipoComida> listar() {
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

    private TipoComida toDomain(TipoComidaJpaEntity entity) {
        return TipoComida.reconstruir(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.isActivo()
        );
    }
}
