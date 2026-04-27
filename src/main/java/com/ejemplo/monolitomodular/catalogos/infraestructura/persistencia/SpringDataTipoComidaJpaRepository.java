package com.ejemplo.monolitomodular.catalogos.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTipoComidaJpaRepository extends JpaRepository<TipoComidaJpaEntity, UUID> {

    boolean existsByIdAndActivoTrue(UUID id);

    boolean existsByNombreIgnoreCase(String nombre);

    List<TipoComidaJpaEntity> findAllByOrderByNombreAsc();
}
