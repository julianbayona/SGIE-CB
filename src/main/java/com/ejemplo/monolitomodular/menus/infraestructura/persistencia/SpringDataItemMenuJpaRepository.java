package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataItemMenuJpaRepository extends JpaRepository<ItemMenuJpaEntity, UUID> {

    List<ItemMenuJpaEntity> findBySeleccionMenuId(UUID seleccionMenuId);
}
