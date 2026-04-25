package com.ejemplo.monolitomodular.salones.infraestructura.persistencia;

import com.ejemplo.monolitomodular.salones.dominio.modelo.Salon;
import com.ejemplo.monolitomodular.salones.dominio.puerto.salida.SalonRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SalonJpaRepositoryAdapter implements SalonRepository {

    private final SpringDataSalonJpaRepository repository;

    public SalonJpaRepositoryAdapter(SpringDataSalonJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Salon guardar(Salon salon) {
        LocalDateTime now = LocalDateTime.now();
        SalonJpaEntity entity = new SalonJpaEntity(
                salon.getId(),
                salon.getNombre(),
                salon.getCapacidad(),
                salon.getDescripcion(),
                salon.isActivo(),
                now,
                now
        );
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<Salon> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Salon> listar() {
        return repository.findAllByOrderByNombreAsc().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Salon> buscarTodosPorIds(Collection<UUID> ids) {
        return repository.findByIdIn(ids).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existePorNombre(String nombre) {
        return repository.existsByNombreIgnoreCase(nombre);
    }

    private Salon toDomain(SalonJpaEntity entity) {
        return Salon.reconstruir(
                entity.getId(),
                entity.getNombre(),
                entity.getCapacidad(),
                entity.getDescripcion(),
                entity.isActivo()
        );
    }
}
