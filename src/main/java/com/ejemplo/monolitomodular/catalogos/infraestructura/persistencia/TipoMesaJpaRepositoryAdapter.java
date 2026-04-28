package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import com.ejemplo.monolitomodular.catalogos.dominio.modelo.TipoMesa;
import com.ejemplo.monolitomodular.catalogos.dominio.puerto.salida.TipoMesaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TipoMesaJpaRepositoryAdapter implements TipoMesaRepository {

    private final SpringDataTipoMesaJpaRepository repository;

    public TipoMesaJpaRepositoryAdapter(SpringDataTipoMesaJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public TipoMesa guardar(TipoMesa tipoMesa) {
        LocalDateTime now = LocalDateTime.now();
        return toDomain(repository.save(new TipoMesaJpaEntity(
                tipoMesa.getId(),
                tipoMesa.getNombre(),
                tipoMesa.isActivo(),
                now,
                now
        )));
    }

    @Override
    public Optional<TipoMesa> buscarPorId(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<TipoMesa> listar() {
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

    private TipoMesa toDomain(TipoMesaJpaEntity entity) {
        return TipoMesa.reconstruir(entity.getId(), entity.getNombre(), entity.isActivo());
    }
}
