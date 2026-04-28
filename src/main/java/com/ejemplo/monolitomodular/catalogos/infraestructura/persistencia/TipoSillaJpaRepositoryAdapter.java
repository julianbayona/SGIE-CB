package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoSilla;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoSillaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TipoSillaJpaRepositoryAdapter implements TipoSillaRepository {

    private final SpringDataTipoSillaJpaRepository repository;

    public TipoSillaJpaRepositoryAdapter(SpringDataTipoSillaJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public TipoSilla guardar(TipoSilla tipoSilla) {
        LocalDateTime now = LocalDateTime.now();
        return toDomain(repository.save(new TipoSillaJpaEntity(
                tipoSilla.getId(),
                tipoSilla.getNombre(),
                tipoSilla.isActivo(),
                now,
                now
        )));
    }

    @Override
    public Optional<TipoSilla> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<TipoSilla> listar() {
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

    private TipoSilla toDomain(TipoSillaJpaEntity entity) {
        return TipoSilla.reconstruir(entity.getId(), entity.getNombre(), entity.isActivo());
    }
}
