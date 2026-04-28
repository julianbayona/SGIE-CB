package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Mantel;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.MantelRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MantelJpaRepositoryAdapter implements MantelRepository {

    private final SpringDataMantelJpaRepository repository;

    public MantelJpaRepositoryAdapter(SpringDataMantelJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mantel guardar(Mantel mantel) {
        LocalDateTime now = LocalDateTime.now();
        return toDomain(repository.save(new MantelJpaEntity(
                mantel.getId(),
                mantel.getNombre(),
                mantel.getColorId(),
                mantel.isActivo(),
                now,
                now
        )));
    }

    @Override
    public Optional<Mantel> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Mantel> listar() {
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

    private Mantel toDomain(MantelJpaEntity entity) {
        return Mantel.reconstruir(entity.getId(), entity.getNombre(), entity.getColorId(), entity.isActivo());
    }
}
