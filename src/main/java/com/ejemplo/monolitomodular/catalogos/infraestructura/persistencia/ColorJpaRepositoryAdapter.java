package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Color;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.ColorRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ColorJpaRepositoryAdapter implements ColorRepository {

    private final SpringDataColorJpaRepository repository;

    public ColorJpaRepositoryAdapter(SpringDataColorJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Color guardar(Color color) {
        LocalDateTime now = LocalDateTime.now();
        return toDomain(repository.save(new ColorJpaEntity(
                color.getId(),
                color.getNombre(),
                color.getCodigoHex(),
                color.isActivo(),
                now,
                now
        )));
    }

    @Override
    public Optional<Color> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Color> listar() {
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

    private Color toDomain(ColorJpaEntity entity) {
        return Color.reconstruir(entity.getId(), entity.getNombre(), entity.getCodigoHex(), entity.isActivo());
    }
}
