package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTipoMomentoMenuJpaRepository extends JpaRepository<TipoMomentoMenuJpaEntity, UUID> {

    boolean existsByIdAndActivoTrue(UUID id);

    boolean existsByNombreIgnoreCase(String nombre);

    List<TipoMomentoMenuJpaEntity> findAllByOrderByNombreAsc();
}
