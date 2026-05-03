package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataPlatoJpaRepository extends JpaRepository<PlatoJpaEntity, UUID> {
}
