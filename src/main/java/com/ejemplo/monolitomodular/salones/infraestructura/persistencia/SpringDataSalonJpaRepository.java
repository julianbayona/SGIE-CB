package com.ejemplo.monolitomodular.salones.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface SpringDataSalonJpaRepository extends JpaRepository<SalonJpaEntity, UUID> {

    boolean existsByNombreIgnoreCase(String nombre);

    List<SalonJpaEntity> findByIdIn(Collection<UUID> ids);

    List<SalonJpaEntity> findAllByOrderByNombreAsc();
}
