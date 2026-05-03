package com.ejemplo.monolitomodular.menus.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataSeleccionMenuJpaRepository extends JpaRepository<SeleccionMenuJpaEntity, UUID> {

    List<SeleccionMenuJpaEntity> findByMenuId(UUID menuId);
}
