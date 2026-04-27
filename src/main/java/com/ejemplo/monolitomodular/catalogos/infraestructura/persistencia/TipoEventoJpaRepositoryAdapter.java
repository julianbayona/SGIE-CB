package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoEvento;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoEventoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TipoEventoJpaRepositoryAdapter implements TipoEventoRepository {

    private final SpringDataTipoEventoJpaRepository repository;

    public TipoEventoJpaRepositoryAdapter(SpringDataTipoEventoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public TipoEvento guardar(TipoEvento tipoEvento) {
        LocalDateTime now = LocalDateTime.now();
        TipoEventoJpaEntity entity = new TipoEventoJpaEntity(
                tipoEvento.getId(),
                tipoEvento.getNombre(),
                tipoEvento.getDescripcion(),
                tipoEvento.isActivo(),
                now,
                now
        );
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<TipoEvento> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<TipoEvento> listar() {
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

    private TipoEvento toDomain(TipoEventoJpaEntity entity) {
        return TipoEvento.reconstruir(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.isActivo()
        );
    }
}
