package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import com.ejemplo.monolitomodular.menus.dominio.modelo.Plato;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.PlatoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
    public Plato guardar(Plato plato) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = platoRepository.findById(plato.getId())
                .map(PlatoJpaEntity::getCreatedAt)
                .orElse(now);
        PlatoJpaEntity entity = new PlatoJpaEntity(
                plato.getId(),
                plato.getNombre(),
                plato.getDescripcion(),
                plato.getPrecioBase(),
                plato.isActivo(),
                createdAt,
                now
        );
        return toDomain(platoRepository.save(entity));
    }

    @Override
    public Optional<Plato> buscarPorId(UUID id) {
        return platoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Plato> listar() {
        return platoRepository.findAllByOrderByNombreAsc().stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return platoRepository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    public boolean existeActivoParaMomento(UUID platoId, UUID tipoMomentoId) {
        return platoMomentoRepository.existsActivoByPlatoIdAndTipoMomentoId(platoId, tipoMomentoId);
    }

    private Plato toDomain(PlatoJpaEntity entity) {
        return Plato.reconstruir(entity.getId(), entity.getNombre(), entity.getDescripcion(), entity.getPrecioBase(), entity.isActivo());
    }
}
