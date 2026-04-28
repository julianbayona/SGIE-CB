package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoAdicional;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoAdicionalRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TipoAdicionalJpaRepositoryAdapter implements TipoAdicionalRepository {

    private final SpringDataTipoAdicionalJpaRepository repository;

    public TipoAdicionalJpaRepositoryAdapter(SpringDataTipoAdicionalJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public TipoAdicional guardar(TipoAdicional tipoAdicional) {
        LocalDateTime now = LocalDateTime.now();
        return toDomain(repository.save(new TipoAdicionalJpaEntity(
                tipoAdicional.getId(),
                tipoAdicional.getNombre(),
                tipoAdicional.getModoCobro(),
                tipoAdicional.getPrecioBase(),
                tipoAdicional.isActivo(),
                now,
                now
        )));
    }

    @Override
    public Optional<TipoAdicional> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<TipoAdicional> listar() {
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

    private TipoAdicional toDomain(TipoAdicionalJpaEntity entity) {
        return TipoAdicional.reconstruir(
                entity.getId(),
                entity.getNombre(),
                entity.getModoCobro(),
                entity.getPrecioBase(),
                entity.isActivo()
        );
    }
}
