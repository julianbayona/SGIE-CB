package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataPlatoJpaRepository extends JpaRepository<PlatoJpaEntity, UUID> {
    List<PlatoJpaEntity> findByActivoTrue();
}
