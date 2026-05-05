package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import com.ejemplo.monolitomodular.menus.dominio.modelo.Plato;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.PlatoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PlatoJpaRepositoryAdapter implements PlatoRepository {

    private final SpringDataPlatoJpaRepository platoRepository;
    private final SpringDataPlatoMomentoJpaRepository platoMomentoRepository;

    public PlatoJpaRepositoryAdapter(SpringDataPlatoJpaRepository platoRepository, SpringDataPlatoMomentoJpaRepository platoMomentoRepository) {
        this.platoRepository = platoRepository;
        this.platoMomentoRepository = platoMomentoRepository;
    }

    @Override
    public Optional<Plato> buscarPorId(UUID id) {
        return platoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existeActivoParaMomento(UUID platoId, UUID tipoMomentoId) {
        return platoMomentoRepository.existsActivoByPlatoIdAndTipoMomentoId(platoId, tipoMomentoId);
    }

    @Override
    public List<Plato> listarActivos() {
        return platoRepository.findByActivoTrue().stream()
                .map(this::toDomain)
                .toList();
    }

    private Plato toDomain(PlatoJpaEntity entity) {
        return Plato.reconstruir(entity.getId(), entity.getNombre(), entity.getDescripcion(), entity.getPrecioBase(), entity.isActivo());
    }
}
