package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.Sobremantel;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.SobremantelRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SobremantelJpaRepositoryAdapter implements SobremantelRepository {

    private final SpringDataSobremantelJpaRepository repository;

    public SobremantelJpaRepositoryAdapter(SpringDataSobremantelJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Sobremantel guardar(Sobremantel sobremantel) {
        LocalDateTime now = LocalDateTime.now();
        return toDomain(repository.save(new SobremantelJpaEntity(
                sobremantel.getId(),
                sobremantel.getNombre(),
                sobremantel.getColorId(),
                sobremantel.isActivo(),
                now,
                now
        )));
    }

    @Override
    public Optional<Sobremantel> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Sobremantel> listar() {
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

    private Sobremantel toDomain(SobremantelJpaEntity entity) {
        return Sobremantel.reconstruir(entity.getId(), entity.getNombre(), entity.getColorId(), entity.isActivo());
    }
}
