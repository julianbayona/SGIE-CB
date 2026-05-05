package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.PlatoMomentoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class PlatoMomentoJpaRepositoryAdapter implements PlatoMomentoRepository {

    private final SpringDataPlatoMomentoJpaRepository repository;

    public PlatoMomentoJpaRepositoryAdapter(SpringDataPlatoMomentoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void asociar(UUID platoId, UUID tipoMomentoId) {
        repository.save(new PlatoMomentoJpaEntity(platoId, tipoMomentoId));
    }

    @Override
    public void eliminar(UUID platoId, UUID tipoMomentoId) {
        repository.deleteByPlatoIdAndTipoMomentoId(platoId, tipoMomentoId);
    }

    @Override
    public boolean existe(UUID platoId, UUID tipoMomentoId) {
        return repository.existsByPlatoIdAndTipoMomentoId(platoId, tipoMomentoId);
    }

    @Override
    public List<RelacionPlatoMomento> listar() {
        return repository.findAllByOrderByPlatoIdAscTipoMomentoIdAsc().stream().map(this::toDomain).toList();
    }

    @Override
    public List<RelacionPlatoMomento> listarPorPlatoId(UUID platoId) {
        return repository.findByPlatoIdOrderByTipoMomentoIdAsc(platoId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<RelacionPlatoMomento> listarPorTipoMomentoId(UUID tipoMomentoId) {
        return repository.findByTipoMomentoIdOrderByPlatoIdAsc(tipoMomentoId).stream().map(this::toDomain).toList();
    }

    private RelacionPlatoMomento toDomain(PlatoMomentoJpaEntity entity) {
        return new RelacionPlatoMomento(entity.getPlatoId(), entity.getTipoMomentoId());
    }
}
