package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataMenuJpaRepository extends JpaRepository<MenuJpaEntity, UUID> {

    Optional<MenuJpaEntity> findByReservaId(UUID reservaId);
}
