package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import com.ejemplo.monolitomodular.menus.dominio.modelo.TipoMomentoMenu;
import com.ejemplo.monolitomodular.menus.dominio.puerto.salida.TipoMomentoMenuRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TipoMomentoMenuJpaRepositoryAdapter implements TipoMomentoMenuRepository {

    private final SpringDataTipoMomentoMenuJpaRepository repository;

    public TipoMomentoMenuJpaRepositoryAdapter(SpringDataTipoMomentoMenuJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<TipoMomentoMenu> buscarPorId(UUID id) {
        return repository.findById(id).map(entity -> TipoMomentoMenu.reconstruir(entity.getId(), entity.getNombre(), entity.isActivo()));
    }

    @Override
    public boolean existeActivoPorId(UUID id) {
        return repository.existsByIdAndActivoTrue(id);
    }
}
