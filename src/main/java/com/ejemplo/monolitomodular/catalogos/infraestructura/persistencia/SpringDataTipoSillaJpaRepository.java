package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTipoSillaJpaRepository extends JpaRepository<TipoSillaJpaEntity, UUID> {

    boolean existsByIdAndActivoTrue(UUID id);

    boolean existsByNombreIgnoreCase(String nombre);

    List<TipoSillaJpaEntity> findAllByOrderByNombreAsc();
}
